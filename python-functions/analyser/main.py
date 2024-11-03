# 
# common imports
# 
import os
import re
import glob
from datetime import datetime, timezone

import yaml
import uuid as uuid_lib

# 
# Domain Models
# 
from scanner import ScanningProcess, FileInfo, Storage, ScanningRecord, StorageStatus, StorageType

# 
# Common Helper functions
# 
from scanner import now_time_str, now_time_str_ymd_hms, random_uuid_str

from scanner import FakeDatabase



DB = FakeDatabase()




scan_proc = ScanningProcess()
print("scan_proc.uuid: ",scan_proc.get_uuid())

demo_file_info = FileInfo()
demo_file_info.path='xxx-yyyy-zzz/test.txt'
# print('demo_file_info.get_path(): ',demo_file_info.get_path())
print('demo_file_info: ', demo_file_info)
print('demo_file_info: ', type(demo_file_info), type(demo_file_info).__name__)
print('type(None): ', type(None), type(None).__name__)

assert 1==1
# assert 1!=1 # causes 'AssertionError'

scan_rec = ScanningRecord.create_from_path(scan_proc, '/test')
print('scan_rec: ',scan_rec.get_uuid())