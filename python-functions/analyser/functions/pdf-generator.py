import os
from datetime import datetime
from dataclasses import dataclass

from reportlab.lib.pagesizes import A4
from reportlab.lib.units import cm, mm
from reportlab.pdfgen import canvas
from reportlab.platypus import (
    BaseDocTemplate,
    SimpleDocTemplate,
    Paragraph,
    Spacer,
    Frame,
    PageTemplate,
    NextPageTemplate,
)
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle, StyleSheet1
from reportlab.lib.enums import TA_LEFT


# 定义页面尺寸和边距
PAGE_WIDTH, PAGE_HEIGHT = A4
MARGIN = 0.5 * cm


# reference doc:
# Reportlab: How to Add Page Numbers - Mouse Vs Python
# https://www.blog.pythonlibrary.org/2013/08/12/reportlab-how-to-add-page-numbers/
class PageNumCanvas(canvas.Canvas):
    """
    http://code.activestate.com/recipes/546511-page-x-of-y-with-reportlab/
    http://code.activestate.com/recipes/576832/
    """

    # ----------------------------------------------------------------------
    def __init__(self, *args, **kwargs):
        """Constructor"""
        canvas.Canvas.__init__(self, *args, **kwargs)
        self.pages = []

    # ----------------------------------------------------------------------
    def showPage(self):
        """
        On a page break, add information to the list
        """
        self.pages.append(dict(self.__dict__))
        self._startPage()

    # ----------------------------------------------------------------------
    def save(self):
        """
        Add the page number to each page (page x of y)
        """
        page_count = len(self.pages)

        for page in self.pages:
            self.__dict__.update(page)
            self.draw_page_number(page_count)
            canvas.Canvas.showPage(self)

        canvas.Canvas.save(self)

    # ----------------------------------------------------------------------
    def draw_page_number(self, page_count):
        """
        Add the page number
        """
        page = "Page %s of %s" % (self._pageNumber, page_count)
        self.setFont("Helvetica", 9)
        self.drawRightString(100 * mm, (PAGE_HEIGHT - 10), page)  # show
        self.drawRightString(100 * mm, 2 * mm, page)  # show


# 定义两栏的宽度和间距
COLUMN_WIDTH = (PAGE_WIDTH - 2 * MARGIN - 0.5 * cm) / 2  # 每栏宽度
COLUMN_SPACING = 0.3 * cm  # 两栏之间的间距


@dataclass
class PdfA4Writer:
    styles: StyleSheet1 = None
    style_normal = None

    def __init__(self) -> None:
        # 定义段落样式
        self.styles = getSampleStyleSheet()

        style_normal = self.styles["Normal"]
        style_normal.fontSize = 11
        style_normal.leading = 10  # 行间距
        style_normal.spaceBefore = 2  # 段前间距, reduce the space between paragraphs
        style_normal.spaceAfter = 2  # 段后间距, reduce the space between paragraphs
        style_normal.alignment = TA_LEFT  # 左对齐
        self.style_normal = style_normal

    # 读取文本文件
    def read_text_file(self, file_path):
        with open(file_path, "r", encoding="utf-8") as file:
            content = file.read()
        return content

    # 将文本内容分割成段落，并处理换行符
    def split_into_paragraphs(self, text):
        paragraphs = text.split("\n")
        return [p.strip() for p in paragraphs if p.strip()]

    # 生成PDF
    def generate_pdf(self, input_file, output_file):
        content = self.read_text_file(input_file)
        paragraphs = self.split_into_paragraphs(content)

        # doc = SimpleDocTemplate(
        #     output_file,
        #     pagesize=A4,
        #     leftMargin=MARGIN,
        #     rightMargin=MARGIN,
        #     topMargin=MARGIN,
        #     bottomMargin=MARGIN,
        # )

        # reference doc: python - Reportlab. Floating Text with two Columns - Stack Overflow
        # https://stackoverflow.com/questions/13720357/reportlab-floating-text-with-two-columns
        doc = BaseDocTemplate(output_file, showBoundary=1)

        # 创建两栏的框架
        leftFrame = Frame(
            MARGIN, MARGIN, COLUMN_WIDTH, PAGE_HEIGHT - 2 * MARGIN, showBoundary=1
        )
        rightFrame = Frame(
            MARGIN + COLUMN_WIDTH + COLUMN_SPACING,
            MARGIN,
            COLUMN_WIDTH,
            PAGE_HEIGHT - 2 * MARGIN,
            showBoundary=1,
        )

        # 创建页面模板
        two_column_template = PageTemplate(
            id="TwoColumns", frames=[leftFrame, rightFrame]
        )

        # 添加页面模板到文档
        doc.addPageTemplates([two_column_template])

        # 创建故事（Story）
        story = []

        for paragraph in paragraphs:
            p = Paragraph(paragraph, self.style_normal)
            story.append(p)
            story.append(Spacer(1, 6))  # 段落之间添加一些间距

        # 构建PDF
        doc.build(story, canvasmaker=PageNumCanvas)


def new_file_path(old_file_name: str, new_file_ext: str) -> str:
    fp_no_ext = os.path.splitext(old_file_name)[0]
    return (
        fp_no_ext
        + "-"
        + datetime.now().strftime("%Y_%m_%d_%H_%M_%S_%f")
        + "."
        + new_file_ext
    )


if __name__ == "__main__":
    srt_files = [
        "/tmp/" + p
        for p in [
            "1.srt",
            "2.srt",
            "3.srt",
        ]
    ]
    for input_file in srt_files:
        output_file = new_file_path(input_file, "pdf")  # 输出PDF文件路径
        print("input_file: ", input_file, "output_file:", output_file)
        pdfA4Writer = PdfA4Writer()
        pdfA4Writer.generate_pdf(input_file, output_file)
