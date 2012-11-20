#!/usr/bin/env python2
import socket, re
from urlparse import urlparse,urlsplit

def parse(url):
  result = urlparse(url)
  ret = {}
  if result[0] != 'http':
    raise RuntimeError('Unsupport Protocol ' + result['scheme'])
  if ':' in result[1]:
    ret['host'] = result[1][:result[1].find(':')]
    ret['port'] = result[1][result[1].find(':')+1:]
  else:
    ret['host'] = result[1]
    ret['port'] = '80'
  ret['scheme'] = result[0]
  ret['path'] = result[2]
  if result[2] == '':
    ret['path'] = '/'
  ret['query'] = result[4]
  ret['fragment'] = result[3]
  return ret
  pass


def retrieve(host, port, path="/"):
  sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  sock.connect((host, int(port)))
  msg = 'GET {!s} HTTP/1.1\r\nHost: {!s}:{!s}\r\nUser-Agent: MiniBrowser 1.0\r\nAccept: text/xml,application/xml,application/xhtml+xml,text/html\r\nAccept-Language: en_us,en;q=0.5\r\nAccept-Encoding: deflate\r\nAccept-Charset: utf-8;q=0.7,*;q=0.7\r\nConnection: close\r\nCache-Control: max-age=0\r\n\r\n'.format(path, host, str(port))
  sock.send(msg)
  buff = ''
  header = {}
  while 1:
    data = sock.recv(4096)
    if not data:
      break
    buff = buff + data
  while '\r\n' in buff:
    if buff.find('\r\n') == 0:
      buff = buff[2:]
      break
    line = buff[:buff.find('\r\n')]
    if ': ' in line:
      header[line[:line.find(': ')]] = line[line.find(': ') + 2:]
    else:
      header['Http-Version'] = line[:line.find(' ')]
      line = line[line.find(' ') + 1:]
      header['Http-Code'] = line[:line.find(' ')]
    buff = buff[buff.find('\r\n')+2:]

  return (header, buff)

def navigate(url):
  print 'Navigating ' + url
  url = parse(url)
  (header, content) = retrieve(url['host'], url['port'], url['path'] + '?' + url['query'])
  while header['Http-Code'][0] == '3':
    if header['Http-Code'] == '302':
      return navigate(header['Location'])
  return content
  pass

if __name__ == "__main__":
  url = raw_input('Enter URL: ');
  print navigate(url)
  pass

