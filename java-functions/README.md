# Great Learning Material

## the official guideline about how to index files

https://lucene.apache.org/core/9_12_0/index.html

17th Oct. 2024 DONE

## how to change committer name in git

Changing the Git user inside Visual Studio Code - Stack Overflow
https://stackoverflow.com/questions/42318673/changing-the-git-user-inside-visual-studio-code

```shell
git config user.useConfigOnly true --global

git config  user.name "xxx"
git config  user.email "xxx@xxx.xxx"

git config --list --show-origin
```

# MongoDB

## MongoDB Compass Download (GUI) | MongoDB

https://www.mongodb.com/try/download/atlascli

## Install MongoDB Community Edition on macOS - MongoDB Manual

https://www.mongodb.com/docs/manual/tutorial/install-mongodb-on-os-x/#install-mongodb-community-edition

### useful commands

To verify that MongoDB is running, perform one of the following:

If you started MongoDB as a macOS service:

```shell
brew services list
```

You should see the service mongodb-community listed as started.

If you started MongoDB manually as a background process:

```shell
ps aux | grep -v grep | grep mongod
```

You should see your mongod process in the output.

You can also view the log file to see the current status of your mongod process: /usr/local/var/log/mongodb/mongo.log.