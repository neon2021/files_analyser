from datetime import datetime

import uuid as uuid_lib

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