#!/usr/bin/env python2
#coding=utf-8
import socket,sys,os
import time,signal,thread
import urllib

CONTENT_400 = b'<html><body><h1>400 Bad Request</h1></body></html>'
CONTENT_403 = b'<html><body><h1>403 Forbidden</h1></body></html>'
CONTENT_404 = b'<html><body><h1>404 Not Found</h1></body></html>'
CONTENT_405 = b'<html><body><h1>405 Method Not Allowed</h1></body></html>'
CONTENT_500 = b'<html><body><h1>500 Internel Server Error</h1></body></html>'

class Server:
  def __init__(self, host= '', port = 80, www_dir = 'www'):
    self.host = ''
    self.port = port
    self.www_dir = os.path.abspath(www_dir)

  def start(self):
    try:
      self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
      print "Launching HTTP server on " + str(self.host) + ":" + str(self.port)
      self.socket.bind((self.host, self.port))
    except Exception as e:
      print "ERROR: Cound not acquire port " + str(self.port)
      sys.exit(1)
    print "Server successfully launched!"
    self._start_daemon()
    #daemon_thread = thread.start_new_thread(self._start_daemon,())
    #daemon_thread.join()

  def shutdown(self):
    try:
      print "Shutting down the server"
      s.socket.shutdown(socket.SHUT_DOWN)
    except Exception as e:
      print "WARNING: Could not shut down the socket, maybe already closed."

  def _get_header(self, code, option={}):
    if (code == 200):
      header = 'HTTP/1.1 200 OK\r\n'
    elif (code == 400):
      header = 'HTTP/1.1 400 Bad Request\r\n'
    elif (code == 404):
      header = 'HTTP/1.1 404 Not Found\r\n'
    elif (code == 405):
      header = 'HTTP/1.1 405 Method Not Allowed\r\n'
    elif (code == 403):
      header = 'HTTP/1.1 403 Forbidden\r\n'
    elif (code == 500):
      header = 'HTTP/1.1 500 Internel Server Error\r\n'

    current_date = time.strftime("%a, %d %b %Y %H:%M:%S", time.localtime())
    header += 'Date: ' + current_date + "\r\n"
    header += 'Server: MiniHTTPServer\r\n'
    if ('Content-Length' in option):
      header += 'Content-Length: ' + str(option['Content-Length']) + '\r\n'
    header += 'Connection: close\r\n\r\n'
    return header

  def _handler(self, conn, addr):
    addr = str(addr[0]) + ":" + str(addr[1])
    print "New connection from " + addr

    data = conn.recv(4096)
    string = bytes.decode(data)

    print addr + ": header"
    print "----------------------"
    print data
    print "----------------------"

    string = [x for x in string.split(' ') if x]
    if (len(string) > 0):
      request_method = string[0]
    else:
      request_method = 'random string'
    if (len(string) > 1):
      file_requested = string[1].split('?')[0]
    else:
      file_requested = 'random string'
    if (request_method == 'GET') | (request_method == 'HEAD'):
      file_requested = os.path.normpath(urllib.unquote(file_requested.encode('utf-8')))
      if (file_requested[0] != '/'):
        print addr + ': bad request, not absoluteURI'
        if (request_method == 'GET'):
          content = CONTENT_400
        header = self._get_header(400, {'Content-Length' : len(content)})
      else:
        if (file_requested.endswith('/')):
          file_requested += 'index.html'

        file_requested = os.path.abspath(self.www_dir + file_requested)
        print addr + ": requesting " + file_requested

        content = ''
        try:
          if (os.path.commonprefix((file_requested, self.www_dir + '/')) != self.www_dir + '/'):
            print addr + ": violating directory"
            raise IOError(13, 13)
          f = open(file_requested, 'rb')
          if (request_method == 'GET'):
            content = f.read()
          f.close()
          header = self._get_header(200, {'Content-Length' : len(content)})
          print addr + ': 200 OK'
        except IOError as e:
          if (e.errno == 13):
            if (request_method == 'GET'):
              content = CONTENT_403
            header = self._get_header(403, {'Content-Length' : len(content)})
            print addr + ': 403 Forbidden'
          #elif (e.errno == 2):
          else:
            if (request_method == 'GET'):
              content = CONTENT_404
            header = self._get_header(404, {'Content-Length' : len(content)})
            print addr + ': 404 Not Found'
        except Exception as e:
          if (request_method == 'GET'):
            content = CONTENT_500
          header = self._get_header(500, {'Content-Length' : len(content)})
          print addr + ': Unknown Error'
    else:
      content = CONTENT_405
      header = self._get_header(405, {'Content-Length' : len(content)})

    response = header.encode()
    response += content

    conn.send(response)
    conn.close()
    print addr + ': Connection closed'
    pass

  def _start_daemon(self):
    while True:
      self.socket.listen(100)
      conn, addr = self.socket.accept()
      thread.start_new_thread(self._handler, (conn, addr))
    pass

def shutdown_server(sig, dummy):
  server.shutdown()
  sys.exit(1)

if __name__ == "__main__":
  signal.signal(signal.SIGINT, shutdown_server)
  server = Server(port = 8080)
  server.start()
  pass

