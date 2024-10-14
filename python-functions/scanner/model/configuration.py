import os
import re

import yaml

class Config(object):

    def __init__(self, config_abs_path:str) -> None:
        self.config_path = config_abs_path
        
        self.config = self.read_cfg()
        
    def read_cfg(self):
        '''
        Read global configuration and create global DB
        '''
        with open(os.path.expanduser(self.config_path),'r') as file:
            config = yaml.load(file,Loader=yaml.FullLoader)
        print("config:",config)
        return config

    def init_storage_info(self):
        '''
        initialization of storages infomation
        '''
        uuid_to_abs_path={}
        for rootdir, dirtype in self.config['storage-locations'].items():
            try:
                for path in os.listdir(rootdir):
                    # print("path: ",path)
                    if path.endswith('.storage-location-id'):
                        # print("id path: ",path)
                        uuid = re.sub(r'(.+)\.storage-location-id',r'\1',path)
                        uuid_to_abs_path[uuid] = {"uuid":uuid,"path":rootdir,"type":dirtype}
            except Exception as e:
                # print('for scan_proc: ctx: path={path}, error:{errMessage}'.format(path=rootdir, errMessage=repr(e))) # repr(e)="FileNotFoundError(2, 'No such file or directory')"
                print('for scan_proc: ctx: path={path}, error:{errMessage}'.format(path=rootdir, errMessage=str(e)))
                # print('for scan_proc: ctx: path={path}, error:{errMessage}'.format(path=rootdir, errMessage=e)) # e="[Errno 2] No such file or directory: '/Volumes/VOLUME1'"
                # print('for scan_proc: ctx: path={path}, error:{errMessage}'.format(path=rootdir, errMessage=type(e))) # type(e)="<class 'FileNotFoundError'>"
        print('uuid_to_abs_path: ',uuid_to_abs_path)

        print('uuid_to_abs_path.values(): ',uuid_to_abs_path.values())