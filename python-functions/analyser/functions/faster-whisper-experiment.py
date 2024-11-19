# usage:
# 
# pydir="..."
# mediadir="..."
# 
# python "$pydir/faster-whisper-experiment.py" -m faster-whisper-large-v3-turbo-ct2 -fp "$mediadir/xxx.mp4" -fmt srt
# 

# pip install -U huggingface_hub
# export HF_ENDPOINT=https://hf-mirror.com
# huggingface-cli download --resume-download gpt2 --local-dir gpt2
# 
# 
# huggingface-cli download --resume-download distil-whisper/distil-large-v3-ct2 --local-dir ~/Downloads/huggingface_downloads/distil-whisper/distil-large-v3-ct2
# huggingface-cli download --resume-download Systran/faster-distil-whisper-large-v2 --local-dir ~/Downloads/huggingface_downloads/Systran/faster-distil-whisper-large-v2
# huggingface-cli download --resume-download deepdml/faster-whisper-large-v3-turbo-ct2 --local-dir ~/Downloads/huggingface_downloads/deepdml/faster-whisper-large-v3-turbo-ct2
# huggingface-cli download --resume-download Systran/faster-whisper-large-v3 --local-dir ~/Downloads/huggingface_downloads/Systran/faster-whisper-large-v3

import argparse
import os

import torch
from faster_whisper import WhisperModel

from datetime import datetime

parser = argparse.ArgumentParser(
                    prog='ProgramName',
                    description='What the program does',
                    epilog='Text at the bottom of help')
parser.add_argument('-fp', '--filepath')
parser.add_argument('-m', '--model')
parser.add_argument('-fmt', '--format', choices=['txt','srt'])
args = parser.parse_args()

print('args: %s'%(args))

file_path = args.filepath

# DONE: New version not working on fine-tuned Whisper-large-V3 models · Issue #582 · SYSTRAN/faster-whisper
# https://github.com/SYSTRAN/faster-whisper/issues/582
# Solution: pip install faster_whisper==1.0.3
# 
# ERROR: Unable to load any of {libcudnn_ops.so.9.1.0, libcudnn_ops.so.9.1, libcudnn_ops.so.9, libcudnn_ops.so}
# issue: "Downgrade ctranslate2 by jhj0517 · Pull Request #348 · jhj0517/Whisper-WebUI" https://github.com/jhj0517/Whisper-WebUI/pull/348
# solution: pip install ctranslate2==4.4.0
# 
# how to observe the resources usage when GPU is running
# command: nvidia-smi -l 3000

model = args.model if args.model else 'distil-large-v3-ct2'
file_format = args.format if args.format else 'txt'

same_folder=os.path.expanduser("~/Downloads/huggingface_downloads/")
model_dict={
    'distil-large-v3-ct2':same_folder+"/distil-whisper/distil-large-v3-ct2",
    'faster-distil-whisper-large-v2':os.path.join(same_folder,"/Systran/faster-distil-whisper-large-v2"),
    'faster-whisper-large-v3-turbo-ct2':same_folder+"/deepdml/faster-whisper-large-v3-turbo-ct2",
    'faster-whisper-large-v3':same_folder+"/Systran/faster-whisper-large-v3",
    }

selected_model_path = model_dict[model]
print('file_path=%s, model=%s, selected_model_path=%s'%(file_path, model, selected_model_path))


# define our torch configuration
# device = "cuda:0" if torch.cuda.is_available() else "cpu" # bug: ValueError: unsupported device cuda:0
device = "cuda" if torch.cuda.is_available() else "cpu" # fix bug: ValueError: unsupported device cuda:0
compute_type = "float16" if torch.cuda.is_available() else "float32"
print('device=%s, compute_type=%s'%(device, compute_type))

start = datetime.now()
only_file_name = os.path.splitext(os.path.basename(file_path))[0]

file_name_suffix= "_srt.txt" if file_format=='txt' else ".srt"

output_srt_file_path= os.path.join( os.path.dirname(file_path),only_file_name+"_"+start.strftime("%H_%M_%S")+file_name_suffix)
print('output_srt_file_path: %s'%(output_srt_file_path))


# load model on GPU if available, else cpu
# model = WhisperModel(os.path.expanduser("~/Downloads/huggingface_downloads/distil-whisper/distil-large-v3-ct2"), device=device, compute_type=compute_type,local_files_only=True)
model = WhisperModel(selected_model_path, device=device, compute_type=compute_type,local_files_only=True)


segments, info = model.transcribe(file_path, beam_size=5, language="en")

# nice method to convert seconds to the format as "hour:minute:second"
# sourced from "Python Convert Seconds to HH:MM:SS (hours, minutes, seconds)" https://pynative.com/python-convert-seconds-to-hhmmss/
def conver_to_hms(sec:float)->str:
    mm,ss=divmod(sec,60)
    hh,mm=divmod(mm,60)
    return '%02d:%02d:%02d'%(hh,mm,ss)

def create_txt_line(row_num:int, segment)->str:
    ''' for txt format, line:
    [00:56:06 -> 00:56:06] Yeah.
    '''
    return """[%s -> %s] %s\n""" % (
            conver_to_hms(segment.start), conver_to_hms(segment.end)
                , segment.text
        )

def create_srt_line(row_num:int, segment)->str:
    ''' for srt format, line:
    1
    00:56:06,001 --> 00:56:06,901
    Yeah.
    '''
    return """%s
%s,000 --> %s,000
%s\n\n""" % (row_num
            , conver_to_hms(segment.start), conver_to_hms(segment.end)
            , segment.text
        )

with open(output_srt_file_path,'w') as srt_out:
    row_num=1
    for segment in segments:
        if file_format=='txt':
            line = create_txt_line(row_num, segment)
            
        if file_format=='srt':
            line = create_srt_line(row_num, segment)
            
        srt_out.write(line)
        if row_num % 50 ==0:
            srt_out.flush()
        print(line)
        row_num+=1

    ending = datetime.now()
    elapse_seconds = (ending-start).seconds
    # srt_out.write("selected_model_path: %s"%(selected_model_path),ending='\n')
    srt_out.write("\n\n\n")
    srt_out.write("selected_model_path: %s\n"%(selected_model_path))
    srt_out.write("start: %s, end: %s\n"%(start, ending))
    srt_out.write("elapse_seconds: %s\n"%elapse_seconds)