import unittest
import os
import shutil
import gzip
from zipfile import ZipFile

import sys
import time
import pathlib

# directory reach
parent_parent_dir=os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
print('parent_parent_dir:%s'%parent_parent_dir)
analyser_directory = os.path.join(parent_parent_dir,'analyser')
sys.path.append(analyser_directory)
print("sys.path: %s"%sys.path)
from scanner import get_file_md5
    
class TestFileMD5(unittest.TestCase):
    folder_tmp_path:str='tmp'
    folder_a_path:str='tmp/tmp_a'
    folder_b_path:str='tmp/tmp_b'
    folder_a_zip_file_name:str='tmp_a.zip'
    folder_b_zip_file_name:str='tmp_b.zip'
    
    def make_dirs(full_path:str)->None:
        path = pathlib.Path(full_path)
        path.mkdir(parents=True, exist_ok=True)
    
    def test_parent_folder_will_be_affected_by_child_files_writing(self):
        """
        * time 1:
        #. folder A
            #. file A1
            #. file A2
        
        * time 2:
        * after changing A1
        #. folder A
            #. file A1 (changed)
            #. file A2
        * all the system attributes of folder A will be changed, except st_birthtime
        """
        folder_a_path = self.__class__.folder_a_path
        TestFileMD5.make_dirs(folder_a_path)
        folder_a_stat = os.stat(folder_a_path)
        folder_time_dict = TestFileMD5.buildTimeDict(folder_a_stat)
        print('\nfolder_time_dict=%s'%(folder_time_dict))
        
        TestFileMD5.createFileWithFileName(folder_a_path,"file_a1")
        time.sleep(3)
        TestFileMD5.createFileWithFileName(folder_a_path,"file_a2")
        
        print('\n\nscan folder:\n\n')
        for dirpath, dirnames, filenames in os.walk(folder_a_path):
            print('dirnames: ', dirnames)
            for fn in filenames:
                absolute_path = os.path.join(dirpath,fn)
                print('absolute_path: ', absolute_path)
        
        
        folder_a_stat2 = os.stat(folder_a_path)
        folder_time_dict2 = TestFileMD5.buildTimeDict(folder_a_stat2)
        print('after writing two files, folder : \nfolder_time_dict2=%s'%(folder_time_dict2))
        
        # self.assertNotEqual(folder_time_dict,folder_time_dict2,"compare folder system info")
        keys_for_the_same_value=['st_birthtime']
        for key in keys_for_the_same_value:
            self.assertEqual(folder_time_dict[key],folder_time_dict2[key],"found diff values for folder system info with key is [%s]"%(key))
        for key in [k for k in folder_time_dict.keys() if k not in keys_for_the_same_value]:
            self.assertNotEqual(folder_time_dict[key],folder_time_dict2[key],"found the same values for folder system info with key is [%s]"%(key))
    
    def test_whether_compresed_file_consisting_of_a_folder_affected_by_child_files_system_attr_changed(self):
        """
        # time 1:
         folder A
           file A1 (content is "test")
           file A2 (content is "test")
           
        # time 2:
         folder B
           file B1 (content is "test")
           file B2 (content is "test")
    
        # time 3:
         compress folder A and B
         assert that the md5 values for them will be the same
        """
        folder_tmp_path = self.__class__.folder_tmp_path
        folder_a_path = self.__class__.folder_a_path
        folder_b_path = self.__class__.folder_b_path
        folder_a_zip_file_name = self.__class__.folder_a_zip_file_name
        folder_b_zip_file_name = self.__class__.folder_b_zip_file_name
        
        TestFileMD5.make_dirs(folder_a_path)
        files_in_folder_a:list= [os.path.join(folder_a_path, f) for f in ["file_a1","file_a2"]]
        for fp in files_in_folder_a:
            print("fp: %s"%fp)
            TestFileMD5.createFileWithTxt(fp,"test")
            print("file: %s, md5: %s"%(fp, get_file_md5(fp)))
        zip_filepath_for_folder_a=TestFileMD5.compressGzip(folder_tmp_path, folder_a_zip_file_name, files_in_folder_a)
        # zip_filepath_for_folder_a=TestFileMD5.compressZip(folder_tmp_path, folder_a_zip_file_name, files_in_folder_a)
        
        md5_for_folder_a_zip=get_file_md5(zip_filepath_for_folder_a)
        
        TestFileMD5.make_dirs(folder_b_path)
        files_in_folder_b:list=[os.path.join(folder_b_path, f) for f in ["file_b1","file_b2"]]
        for fp in files_in_folder_b:
            print("fp: %s"%fp)
            TestFileMD5.createFileWithTxt(fp,"test")
            print("file: %s, md5: %s"%(fp, get_file_md5(fp)))
        zip_filepath_for_folder_b=TestFileMD5.compressGzip(folder_tmp_path,folder_b_zip_file_name,files_in_folder_b)
        # zip_filepath_for_folder_b=TestFileMD5.compressZip(folder_tmp_path,folder_b_zip_file_name,files_in_folder_b)
        
        md5_for_folder_b_zip=get_file_md5(zip_filepath_for_folder_b)
        
        self.assertEqual(md5_for_folder_a_zip, md5_for_folder_b_zip)

    def compressGzip(folder_path:str, folder_zip_name:str, files_waited_to_compress:list[str])->str:
        zipfilepath=os.path.join(folder_path,folder_zip_name)
        with gzip.open(zipfilepath,compresslevel=9,mode='wb') as zipfileobj:
            for fp in files_waited_to_compress:
                print('file will be add to Gzip file: %s' % fp)
                with open(fp, 'rb') as f:
                    shutil.copyfileobj(f, zipfileobj)
        return zipfilepath
    
    def compressZip(folder_path:str, folder_zip_name:str, files_waited_to_compress:list[str])->str:
        zipfilepath=os.path.join(folder_path,folder_zip_name)
        with ZipFile(zipfilepath,'w') as zipfileobj:
            for fp in files_waited_to_compress:
                print('file will be add to Zip file: %s' % fp)
                zipfileobj.write(fp)
        return zipfilepath
    
    def cleanTestResources(filepath:str)->None:
        if os.path.exists(filepath):
            # os.removedirs(filepath) # OSError: [Errno 66] Directory not empty: 'tmp_a'
            shutil.rmtree(filepath) # DONE: OSError: [Errno 66] Directory not empty: 'tmp_a'
        
    def buildTimeDict(file_stat)->dict:
        return {"st_atime":file_stat.st_atime,"st_birthtime":file_stat.st_birthtime,"st_ctime":file_stat.st_ctime,"st_mtime":file_stat.st_mtime}
    
    def createFileWithFileName(folderPath:str, fileName:str)->None:
        with open(os.path.join(folderPath,fileName),"w") as f:
            f.write(fileName)
    
    def createFileWithTxt(filePath:str, txtContent:str)->None:
        with open(filePath,"w") as f:
            f.write(txtContent)
        
    def setUp(self):
        TestFileMD5.cleanTestResources(self.__class__.folder_a_path)
        TestFileMD5.cleanTestResources(self.__class__.folder_b_path)
        

    def tearDown(self):
        # TestFileMD5.cleanTestResources(self.__class__.folder_a_path)
        # TestFileMD5.cleanTestResources(self.__class__.folder_b_path)
        pass

if __name__=='__main__':
    unittest.main()