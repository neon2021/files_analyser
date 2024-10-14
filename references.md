# About Python

## Python Utilities

python-magic · PyPI
https://pypi.org/project/python-magic/

Python strftime() function - GeeksforGeeks
https://www.geeksforgeeks.org/python-strftime-function/

pyyaml.org/wiki/PyYAMLDocumentation
https://pyyaml.org/wiki/PyYAMLDocumentation

glob — Unix style pathname pattern expansion — Python 3.12.6 documentation
https://docs.python.org/3/library/glob.html#glob.glob

enum — Support for enumerations — Python 3.12.7 documentation
https://docs.python.org/3/library/enum.html

Timeit in Python with Examples - GeeksforGeeks
https://www.geeksforgeeks.org/timeit-python-examples/

## Python Code Standards

Python Naming Conventions - GeeksforGeeks
https://www.geeksforgeeks.org/python-naming-conventions/

PEP 8 – Style Guide for Python Code | peps.python.org
https://peps.python.org/pep-0008/

Python Code Hierarchy is very very Simple | by Ebo Jackson | Medium
https://medium.com/@ebojacky/python-code-hierarchy-is-very-very-simple-f2355f7f3091

Nested Modules - Intermediate Python
https://profound.academy/python-mid/nested-modules-TfJP5l9oDn6ZgGOMSPFZ

# Java

## Lucene for Java

SmartChineseAnalyzer (Lucene 6.4.0 API)
https://lucene.apache.org/core/6_4_0/analyzers-smartcn/org/apache/lucene/analysis/cn/smart/SmartChineseAnalyzer.html

Apache Lucene™ 9.11.1 Documentation
https://lucene.apache.org/core/9_11_1/index.html


# Git

## techniques

```shell
% git remote -v

(output)
origin	git@github.com:neon2021/files_analyser.git (fetch)
origin	git@github.com:neon2021/files_analyser.git (push)
```

```shell
% git push

(output)
# get some error response
```

> check .ssh/config

```shell
... ...

Host neon2021
  HostName github.com
  AddKeysToAgent yes
  UseKeychain yes
  IdentityFile ~/.ssh/id_rsa_xxxxxx
  IdentitiesOnly yes

... ...
```

> modify git config for the repository

```shell
% git remote set-url origin git@neon2021:neon2021/files_analyser.git

% git push

(output successful response)
```