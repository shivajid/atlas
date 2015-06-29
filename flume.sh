#!/bin/bash

flume-ng agent -c /etc/flume/conf -f /etc/flume/conf/flume.conf -n sandbox
