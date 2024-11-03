from datetime import datetime

import uuid as uuid_lib
import hashlib

GLOBAL_DATETIME_FORMAT="%Y-%m-%d %H:%M:%S.%f"
DATETIME_FORMAT_YMD_HMS="%Y_%m_%d_%H_%M_%S"

def now_time_str():
    x = datetime.now()
    return x.strftime(GLOBAL_DATETIME_FORMAT)

def now_time_str_ymd_hms():
    x = datetime.now()
    return x.strftime(DATETIME_FORMAT_YMD_HMS)

def random_uuid_str()->str:
    return str(uuid_lib.uuid4())
    
def get_file_md5(fname:str)->str:
    m = hashlib.md5()   #创建md5对象
    with open(fname,'rb') as fobj:
        while True:
            data = fobj.read(4096)
            if not data:
                break
            m.update(data)  #更新md5对象

    return m.hexdigest()    #返回md5对象