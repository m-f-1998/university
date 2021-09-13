using System;
using System.Net;
using System.IO;
using System.Text.RegularExpressions;

namespace WebBrowser
{
    /// <summary>
    /// Class <c>Connection</c> For Making HTTP/HTTPS Requests
    /// </summary>
    class Connection
    {
        /// <summary>
        /// GET HTTP Request
        /// </summary>
        /// <param name="url"></param>
        /// <param name="contentType"></param>
        /// <returns>String Array Denoting Status Code, Website Title (Optional) and Code (Optional) </returns>
        internal string[] GET( string url, string contentType )
        {
            if ( url == null || contentType == null )
                throw new ArgumentException( "Argument Cannot Be Null" );

            try
            {
                Uri uri = new UriBuilder( url ).Uri;
                HttpWebRequest request = ( HttpWebRequest ) WebRequest.Create( uri );
                request.AutomaticDecompression = DecompressionMethods.GZip | DecompressionMethods.Deflate;
                request.ContentType = contentType;

                // <code>using()</code> closes readers and response automatically after use
                using ( HttpWebResponse response = ( HttpWebResponse ) request.GetResponse() )
                using ( Stream stream = response.GetResponseStream() )
                using ( StreamReader reader = new StreamReader( stream ) )
                {
                    var code = reader.ReadToEnd();
                    var match = new Regex( @"<title>(.*)</title>" ).Match( code );
                    var title = match.Success ? match.Groups[ 0 ].Value.Substring( 7, match.Groups[ 0 ].Value.Length - 15 ) + "\n\n" : "";

                    return new string[] { title, Regex.Replace( String.Format( "\n200 (OK)\n\n{0}{1}", title, code ), "(?<!\r)\n", "\r\n" ) };
                }
            }
            catch ( UriFormatException )
            {
                return new string[] { "Error", "The URI Could Not Be Parsed" };
            }
            catch ( WebException e )
            {
                var response = e.Response as HttpWebResponse;

                if ( response == null || response.GetResponseStream() == Stream.Null )
                {
                    return new string[] { "Error", "Connection To Web Page Could Not Be Established" };
                }
                return new string[] { "Error", ( (int) response.StatusCode ).ToString() + " " + response.StatusDescription };
            }
        }
    }
}
