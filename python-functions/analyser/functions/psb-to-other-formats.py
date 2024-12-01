import os
from datetime import datetime
import argparse

from psd_tools import PSDImage
from PIL import Image


def new_file_path(old_file_name: str, new_file_ext: str) -> str:
    fp_no_ext = os.path.splitext(old_file_name)[0]
    return fp_no_ext + "." + new_file_ext


format_dict = {"jpg": "JPEG", "png": "PNG", "tif": "TIFF"}


def get_valid_format(format: str) -> str:
    return format_dict[format]


def convert_psb_to_image(input_psb, output_format: str):
    format_name_as_param = get_valid_format(output_format)
    new_fp: str = new_file_path(input_psb, output_format)
    print("[%s]"%(datetime.now()),"format_name_as_param:", format_name_as_param, "new_fp:", new_fp)
    psd = PSDImage.open(input_psb)
    print("[%s]"%(datetime.now()),"PSDImage.open done")

    image = psd.composite()
    print("[%s]"%(datetime.now()),"psd.composite done")
    if format_name_as_param == "JPEG":
        image.save(new_fp, format_name_as_param, quality=95)
    elif format_name_as_param == "PNG":
        image.save(new_fp, format_name_as_param)
    elif format_name_as_param == "TIFF":
        image.save(new_fp, format_name_as_param, compression="tiff_lzw")
    else:
        print("no new image is created")
    print("[%s]"%(datetime.now()),"save process done")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        prog="ProgramName",
        description="What the program does",
        epilog="Text at the bottom of help",
    )
    parser.add_argument("-fp", "--filepath")
    args = parser.parse_args()

    print("args: %s" % (args))
    file_path = os.path.expanduser(args.filepath)

    convert_psb_to_image(
        file_path,
        "jpg",
    )
