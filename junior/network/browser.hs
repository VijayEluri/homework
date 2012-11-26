import Network
import Network.URI hiding (path)
import Control.Concurrent
import Text.Regex.Posix
import Control.Applicative
import System.IO
import System.IO.Error (isEOFError)
import Control.Exception

takeInt (Just x) = x
takeInt Nothing = 0

takeStr (Just x) = x
takeStr Nothing = ""

httpCrLf :: String
httpCrLf = "\r\n"

httpGetReq :: String -> Int -> String -> String
httpGetReq host port path = 
    "GET " ++ path ++ httpCrLf ++
    "Host: " ++ host ++ ":" ++ (show port) ++ httpCrLf ++
    "User-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.0; en-US; rv:1.8.0.7) Gecko/20060909 Firefox/1.5.0.7" ++ httpCrLf ++
    "Accept: text/xml,application/xml,application/xhtml+xml,text/html" ++ httpCrLf ++
    "Accept-Language: en-us,en;q=0.5" ++ httpCrLf ++
    "Accept-Encoding: deflate" ++ httpCrLf ++
    "Accept-Charset: utf-8;q=0.7,*;q=0.7" ++ httpCrLf ++
    "Connection: close" ++ httpCrLf ++
    "Cache-Control: max-age=0" ++ httpCrLf ++ httpCrLf

main = withSocketsDo $ do
    putStr "Enter URL: "
    hFlush stdout
    uri <- (fmap parseURI getLine)
    if uri == Nothing then
        putStrLn "Wrong URL"
    else do
        let portStr = (fmap uriPort $ uriAuthority =<< uri)
            path = uriPath <$> uri
            host = uriRegName <$> (uriAuthority =<< uri)
            scheme = uriScheme <$> uri
            query = uriQuery <$> uri
            port = if (portStr == Nothing || portStr == Just "") then Just 80 else fmap ((read :: String->Int) . tail) portStr
            req = httpGetReq <$> host <*> port <*> path
        sock <- connectTo (takeStr host) (PortNumber $ fromIntegral (takeInt port))
   --     (startConn sock) (takeStr req) `catch` handler `finally` hClose sock
    --        where 
     --           handler (IOException e) | isEOFError e = return ()
      --          handler e = putStrLn $ show e
        startConn sock (takeStr req)

startConn :: Handle -> String -> IO ()
startConn sock str = do
    hPutStrLn sock str
    hFlush sock
    input <- hGetContents sock
    putStrLn input
    hClose sock
    putStrLn "INFO: done with socket connect"

