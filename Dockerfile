FROM ubuntu:latest
LABEL authors="krasz"

ENTRYPOINT ["top", "-b"]