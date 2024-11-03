import os
from enum import Enum

from .base import ModelBase

class StorageType(Enum):
    OSX = "osx"
    LINUX = "linux"
    WIN = "win"
    UNKNOWN = "unknown"

class StorageStatus(Enum):
    VALID = "valid"
    IGNORED = "ignored"

class Storage(ModelBase):
    def __init__(self) -> None:
        self.name=None
        self.uuid=None
        self.dir_path=None
        self.type:StorageType=None
        self.reg_time=None
        self.status:StorageStatus=None
        # self.database=None
        
    def get_entity_name(self):
        return "Storage"
    
    # def get_entity_id(self): # cause error: Can't instantiate abstract class FileInfo with abstract method get_entity_id
    def _ModelBase__get_entity_id(self):
        return self.get_uuid()

    def get_name(self):
        return self.name

    def set_name(self, value):
        self.name = value

    def get_uuid(self):
        return self.uuid

    def set_uuid(self, value):
        self.uuid = value

    def get_dir_path(self)->str:
        return self.dir_path

    def set_dir_path(self, value:str):
        print(f'before setting, value: {value}, self.dir_path: {self.dir_path}')
        self.dir_path = value if value.endswith(os.path.sep) else value+os.path.sep
        print(f'after setting, value: {value}, self.dir_path: {self.dir_path}')

    def get_type(self)->StorageType:
        return self.type

    def set_type(self, value:StorageType):
        self.type = value

    def get_reg_time(self):
        return self.reg_time

    def set_reg_time(self, value):
        self.reg_time = value

    def get_status(self)->StorageStatus:
        return self.status

    def set_status(self, value:StorageStatus):
        self.status = value