from datetime import datetime

from .base import ModelBase

from ..utils.helper import *

class FileInfo(ModelBase):
    def __init__(self, path=None, uuid_str=None, scanned_time=None, os_create_time=None, os_modify_time=None, os_access_time=None, scan_proc_uuid=None, hash=None, hash_algo=None, file_size=None, mime_type=None):
        # uuid.uuid4() : https://docs.python.org/zh-cn/3/library/uuid.html#example
        self.uuid:str =random_uuid_str() if uuid_str is None else uuid_str
        self.path:str = path
        self.scanned_time:datetime = scanned_time
        self.os_create_time:str = os_create_time
        self.os_modify_time:str = os_modify_time
        self.os_access_time:str = os_access_time
        self.scan_proc_uuid:str = scan_proc_uuid
        self.hash:str = hash
        self.hash_algo:str = hash_algo
        self.file_size:int = file_size
        self.mime_type:str = mime_type
        
    def get_entity_name(self):
        return "FileInfo"
    
    # def get_entity_id(self): # cause error: Can't instantiate abstract class FileInfo with abstract method get_entity_id
    # sourced from :https://stackoverflow.com/questions/31457855/cant-instantiate-abstract-class-with-abstract-methods
    def _ModelBase__get_entity_id(self):
        return self.get_uuid()
        
    def __format_datetime(self, dt_obj)->str:
        return datetime.strftime(dt_obj, GLOBAL_DATETIME_FORMAT) if dt_obj is not None else None

    def get_os_create_time_str(self):
        return self.__format_datetime(self.os_create_time)

    def get_os_modify_time_str(self):
        return self.__format_datetime(self.os_modify_time)

    def get_os_access_time_str(self):
        return self.__format_datetime(self.os_access_time)

    def get_scanned_time_str(self):
        return self.__format_datetime(self.scanned_time)
    
    def get_uuid(self):
        return self.uuid

    def set_uuid(self, value:str):
        self.uuid = value

    def get_path(self):
        return self.path

    def set_path(self, value):
        self.path = value

    def get_scanned_time(self):
        return self.scanned_time

    def set_scanned_time(self, value):
        self.scanned_time = value

    def get_os_create_time(self):
        return self.os_create_time

    def set_os_create_time(self, value):
        self.os_create_time = value

    def get_os_modify_time(self):
        return self.os_modify_time

    def set_os_modify_time(self, value):
        self.os_modify_time = value

    def get_os_access_time(self):
        return self.os_access_time

    def set_os_access_time(self, value):
        self.os_access_time = value

    def get_scan_proc_uuid(self):
        return self.scan_proc_uuid

    def set_scan_proc_uuid(self, value):
        self.scan_proc_uuid = value

    def get_hash(self):
        return self.hash

    def set_hash(self, value):
        self.hash = value

    def get_hash_algo(self):
        return self.hash_algo

    def set_hash_algo(self, value):
        self.hash_algo = value

    def get_file_size(self):
        return self.file_size

    def set_file_size(self, value):
        self.file_size = value

    def get_mime_type(self):
        return self.mime_type

    def set_mime_type(self, value):
        self.mime_type = value
    
    def __str__(self) -> str:
        # print('__str__ called')
        return self.__repr__()
    
    def __repr__(self) -> str:
        # print('__repr__ called')
        return """FileInfo: uuid={uuid}, path={path}, scanned_time={scanned_time}, os_create_time={os_create_time}, os_modify_time={os_modify_time}, os_access_time={os_access_time}, scan_proc_biz_id={scan_proc_biz_id}, hash={hash}, hash_algo={hash_algo}, file_size={file_size}, mime_type={mime_type}""".format(
            uuid=self.uuid,
            path=self.path,
            scanned_time=self.get_scanned_time_str(),
            os_create_time=self.get_os_create_time_str(),
            os_modify_time=self.get_os_modify_time_str(),
            os_access_time=self.get_os_access_time_str(),
            scan_proc_biz_id=self.scan_proc_uuid,
            hash=self.hash,
            hash_algo=self.hash_algo,
            file_size=self.file_size,
            mime_type=self.mime_type
        )