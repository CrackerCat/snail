@echo off

set project=snail
set version=1.0.0
set builder=.\builder\
set lib=lib
set logs=logs
set runtime=java
set jar=%project%-%version%.jar
set modules="java.sql,java.xml,java.base,java.naming,java.desktop,java.logging,java.scripting,java.management,jdk.unsupported"