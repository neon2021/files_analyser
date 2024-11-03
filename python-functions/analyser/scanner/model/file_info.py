from datetime import datetime
from dataclasses import dataclass

from .base import ModelBase

from ..utils.helper import *


@dataclass
class FileInfo(ModelBase):
    # uuid.uuid4() : https://docs.python.org/zh-cn/3/library/uuid.html#example
    uuid: str = random_uuid_str()
    # reference: why all the fields must have a default value? have a look at : Class inheritance in Python 3.7 dataclasses - Stack Overflow
    # https://stackoverflow.com/questions/51575931/class-inheritance-in-python-3-7-dataclasses
    path: str = None
    scanned_time: datetime = None
    os_create_time: str = None
    os_modify_time: str = None
    os_access_time: str = None
    scan_proc_uuid: str = None
    hash: str = None
    hash_algo: str = None
    file_size: int = None
    mime_type: str = None

    def get_entity_name(self):
        return "FileInfo"

    # def get_entity_id(self): # cause error: Can't instantiate abstract class FileInfo with abstract method get_entity_id
    # sourced from :https://stackoverflow.com/questions/31457855/cant-instantiate-abstract-class-with-abstract-methods
    def _ModelBase__get_entity_id(self):
        return self.get_uuid()

    def __format_datetime(self, dt_obj) -> str:
        return (
            datetime.strftime(dt_obj, GLOBAL_DATETIME_FORMAT)
            if dt_obj is not None
            else None
        )

    def get_os_create_time_str(self):
        return self.__format_datetime(self.os_create_time)

    def get_os_modify_time_str(self):
        return self.__format_datetime(self.os_modify_time)

    def get_os_access_time_str(self):
        return self.__format_datetime(self.os_access_time)

    def get_scanned_time_str(self):
        return self.__format_datetime(self.scanned_time)
