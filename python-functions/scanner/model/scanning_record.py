import os
from datetime import datetime

from .base import ModelBase

from ..utils.helper import *

def decide_file_type(abs_path:str)->str:
    if os.path.isdir(abs_path):
        return "directory"
    elif os.path.isfile(abs_path):
        return "file"
    else:
        return "unknown"
    
class ScanningRecord(ModelBase):
    '''
    ## ScanningRecord (abbreviated as ScanRec)
    '''
    def __init__(self) -> None:
        self.uuid = random_uuid_str()
        self.scan_abs_path=None
        self.scan_time:datetime=None
        self.file_type=None
        self.file_uuid=None
        self.exec_status=None
        self.exec_msg=None
        self.scan_proc_uuid=None
        self.database=None
        
    def get_entity_name(self):
        return "ScanningRecord"
    
    # def get_entity_id(self): # cause error: Can't instantiate abstract class FileInfo with abstract method get_entity_id
    def _ModelBase__get_entity_id(self):
        return self.get_uuid()
        
    @classmethod
    def create_from_path(cls, scan_proc, abs_path:str)->"ScanningRecord":
        
        assert type(scan_proc).__name__ == 'ScanningProcess'
        
        scan_rec = ScanningRecord()
        scan_rec.set_scan_abs_path(abs_path)
        scan_rec.set_scan_time(datetime.now())
        scan_rec.set_file_type(decide_file_type(abs_path))
        scan_rec.set_file_uuid(None)
        scan_rec.set_exec_status("init")
        scan_rec.set_exec_msg(None)
        scan_rec.set_scan_proc_uuid(scan_proc.get_uuid())
        return scan_rec

    def get_uuid(self):
        return self.uuid

    def set_uuid(self, value):
        self.uuid = value

    def get_scan_abs_path(self):
        return self.scan_abs_path

    def set_scan_abs_path(self, value):
        self.scan_abs_path = value

    def get_scan_time(self)->datetime:
        return self.scan_time

    def set_scan_time(self, value:datetime):
        self.scan_time:datetime = value

    def get_file_type(self):
        return self.file_type

    def set_file_type(self, value):
        self.file_type = value

    def get_file_uuid(self):
        return self.file_uuid

    def set_file_uuid(self, value):
        self.file_uuid = value

    def get_exec_status(self):
        return self.exec_status

    def set_exec_status(self, value):
        self.exec_status = value

    def get_exec_msg(self):
        return self.exec_msg

    def set_exec_msg(self, value):
        self.exec_msg = value

    def get_scan_proc_uuid(self):
        return self.scan_proc_uuid

    def set_scan_proc_uuid(self, value):
        self.scan_proc_uuid = value
