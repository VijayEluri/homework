#!/usr/bin/env python2
import socket, re, sys
import threading
from urlparse import urlparse,urlsplit,urljoin

def parse(url):
  result = urlparse(url)
  ret = {}
  if result[0] != 'http':
    raise RuntimeError('Unsupport Protocol ' + result[0])
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

def unparse(url):
  return url['scheme'] + '://' + url['host'] + ':' + url['port'] + url['path'] + '?' + url['query'] + '#' + url['fragment']


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
  ourl = url
  print 'Navigating ' + url
  header = None
  try:
    url = parse(url)
    (header, content) = retrieve(url['host'], url['port'], url['path'] + '?' + url['query'])
    while header['Http-Code'][0] == '3':
      if header['Http-Code'] == '302':
        print '302 Found'
        return navigate(header['Location'])
      elif header['Http-Code'] == '301':
        print '301 Moved Permanently'
        return navigate(header['Location'])
      elif header['Http-Code'] == '307':
        print '307 Temporary Redirect'
        return navigate(header['Location'])
  except Exception as e:
    content = str(e)
  return (header, content, ourl)
  pass

def store(header, content, name):
  if not 'Content-Type' in header:
    header['Content-Type'] = 'text/html'
  if ';' in header['Content-Type']:
    header['Content-Type'] = header['Content-Type'][:header['Content-Type'].find(';')].rstrip()
  file_type = 'text'
  if header['Content-Type'].startswith('image'):
    file_type = header['Content-Type'][header['Content-Type'].find('/') + 1:]
    if (file_type == 'pjpeg') | (file_type == 'jpeg'):
      file_type = 'jpg'
    if (file_type == 'svg+xml'):
      file_type = 'svg'
    if (file_type == 'vnd.microsoft.icon'):
      file_type = 'ico'
  elif header['Content-Type'].startswith('text'):
    file_type = header['Content-Type'][header['Content-Type'].find('/') + 1:]
    if (file_type == 'javascript'):
      file_type = 'js'
  f = open(name + '.' + file_type, 'w')
  f.write(content)
  f.close()
  print 'Request file stored in ' + name + '.' + file_type
  if (file_type == 'html'):
    return True
  return False

def getname(url):
  name = ''
  if '/' in url:
    name = url[url.rfind('/') + 1:]
  if '?' in name:
    name = name[:name.find('?')]
  if '.' in name:
    name = name[:name.rfind('.')]
  if name == '':
    name = 'default'
  return name

def down_img(url):
  (header, content, url) = navigate(url)
  if (header == None):
    print getname(url) + ": " + content
  else:
    store(header, content, getname(url))

if __name__ == "__main__":
  #url = raw_input('Enter URL: ');
  if (len(sys.argv) != 2):
    print 'Usage: ' + sys.argv[0] + ' {url}'
    sys.exit(0)
  url = sys.argv[1]
  (header, content, url) = navigate(url)
  if header == None:
    print content
    sys.exit(0)
  name = getname(url)
  if store(header, content, name):
    print 'Analyzing ' + name + '.html'
    re_img = '<[iI][mM][gG] .*[sS][rR][cC]=[' + "'" + '"](.+?)[' + "'" + '"]'
    img_hunter = re.compile(re_img)
    result = img_hunter.findall(content)
    print str(len(result)) + ' images found'
    threads = []
    for img_url in result:
      img_url = urljoin(url, img_url)
      #(header, content, url) = navigate(img_url)
      tid = threading.Thread(target = down_img, args = (img_url,))
      tid.start()
    pass
  pass

