import socket
import os

from .base import ModelBase

from ..utils.database import Database
from ..utils.helper import *

# how to override methods in abstract class, sourced from : https://www.tutorialspoint.com/python/python_interfaces.htm
class ScanningProcess(ModelBase):
    '''
    ## ScanningProcess (abbreviate as ScanProc)
    '''
    
    def __init__(self) -> None:
        self.uuid:str=random_uuid_str()
        self.start_time:datetime=None
        self.last_update_time:datetime=None
        self.normal_stop_time:datetime=None
        self.scanned_file_count:int=None
        self.scanned_folder_count:int=None
        self.storage_uuid:str=None
        self.storage_path:str=None
        self.host_machine_name:str=None
        self.database:Database=None
        
    def get_entity_name(self):
        return "ScanningProcess"
    
    # def get_entity_id(self): # cause error: Can't instantiate abstract class FileInfo with abstract method get_entity_id
    def _ModelBase__get_entity_id(self):
        return self.get_uuid()
    
    def scan_folder(self, file_count_limit:int, call_back_func):
        cur_scan_proc: ScanningProcess = self
        file_count = 0
        break_all_flag = False
        for dirpath, dirnames, filenames in os.walk(cur_scan_proc.get_storage_path()):
            print('dirnames: ', dirnames)
            for fn in filenames:
                absolute_path = os.path.join(dirpath,fn)
                print('file_count: ',file_count,', absolute_path: ', absolute_path)
                
                if call_back_func:
                    call_back_func(self.database.load('Storage', cur_scan_proc.get_storage_uuid()), absolute_path)
                
                from .scanning_record import ScanningRecord
                scan_rec:ScanningRecord = ScanningRecord.create_from_path(cur_scan_proc, absolute_path)
                self.database.save('ScanningRecord', scan_rec.get_uuid(), scan_rec)
                file_count += 1
                if file_count >= file_count_limit:
                    break_all_flag=True
                    break
            if break_all_flag:
                break
        
    @classmethod
    def create_from(cls, storage, database:Database)->"ScanningProcess":
        
        # cannot declare the parameter 'storage' as a Storage, sourced from : https://www.geeksforgeeks.org/python-circular-imports/
        assert type(storage).__name__=='Storage'
        
        scan_proc = ScanningProcess()
        # sourced from: https://docs.python.org/3/library/socket.html#socket.gethostname
        scan_proc.set_host_machine_name(socket.gethostname())
        scan_proc.set_scanned_file_count(0)
        scan_proc.set_scanned_folder_count(0)
        scan_proc.set_start_time(datetime.now())
        scan_proc.set_storage_path(storage.get_dir_path())
        scan_proc.set_storage_uuid(storage.get_uuid())
        scan_proc.set_uuid(random_uuid_str())
        scan_proc.set_db_instance(database)
        return scan_proc
    
    def get_uuid(self)->str:
        return self.uuid

    def set_uuid(self, value:str):
        self.uuid = value

    def get_start_time(self)->datetime:
        return self.start_time

    def set_start_time(self, value:datetime):
        self.start_time = value

    def get_last_update_time(self)->datetime:
        return self.last_update_time

    def set_last_update_time(self, value:datetime):
        self.last_update_time = value

    def get_normal_stop_time(self)->datetime:
        return self.normal_stop_time

    def set_normal_stop_time(self, value:datetime):
        self.normal_stop_time = value

    def get_scanned_file_count(self):
        return self.scanned_file_count

    def set_scanned_file_count(self, value:int):
        self.scanned_file_count = value

    def get_scanned_folder_count(self):
        return self.scanned_folder_count

    def set_scanned_folder_count(self, value:int):
        self.scanned_folder_count = value

    def get_storage_uuid(self):
        return self.storage_uuid

    def set_storage_uuid(self, value):
        self.storage_uuid = value

    def get_storage_path(self):
        return self.storage_path

    def set_storage_path(self, value):
        self.storage_path = value

    def get_host_machine_name(self):
        return self.host_machine_name

    def set_host_machine_name(self, value):
        self.host_machine_name = value
