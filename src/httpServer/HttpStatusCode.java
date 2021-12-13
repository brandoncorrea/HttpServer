package httpServer;

import java.util.HashMap;
import java.util.Map;

public final class HttpStatusCode {
    public static final int Continue = 100;
    public static final int SwitchingProtocols = 101;
    public static final int OK = 200;
    public static final int Created = 201;
    public static final int Accepted = 202;
    public static final int NonAuthoritativeInformation = 203;
    public static final int NoContent = 204;
    public static final int ResetContent = 205;
    public static final int PartialContent = 206;
    public static final int MultipleChoices = 300;
    public static final int MovedPermanently = 301;
    public static final int Found = 302;
    public static final int SeeOther = 303;
    public static final int NotModified = 304;
    public static final int UseProxy = 305;
    public static final int TemporaryRedirect = 307;
    public static final int BadRequest = 400;
    public static final int Unauthorized = 401;
    public static final int PaymentRequired = 402;
    public static final int Forbidden = 403;
    public static final int NotFound = 404;
    public static final int MethodNotAllowed = 405;
    public static final int NotAcceptable = 406;
    public static final int ProxyAuthenticationRequired = 407;
    public static final int RequestTimeout = 408;
    public static final int Conflict = 409;
    public static final int Gone = 410;
    public static final int LengthRequired = 411;
    public static final int PreconditionFailed = 412;
    public static final int PayloadTooLarge = 413;
    public static final int URITooLong = 414;
    public static final int UnsupportedMediaType = 415;
    public static final int RangeNotSatisfiable = 416;
    public static final int ExpectationFailed = 417;
    public static final int UpgradeRequired = 426;
    public static final int InternalServerError = 500;
    public static final int NotImplemented = 501;
    public static final int BadGateway = 502;
    public static final int ServiceUnavailable = 503;
    public static final int GatewayTimeout = 504;
    public static final int HTTPVersionNotSupported = 505;

    private static final Map<Integer, String> statusCodeDescriptions = new HashMap<Integer, String>() {{
        put(Continue, "Continue");
        put(SwitchingProtocols, "Switching Protocols");
        put(OK, "OK");
        put(Created, "Created");
        put(Accepted, "Accepted");
        put(NonAuthoritativeInformation, "Non-Authoritative Information");
        put(NoContent, "No Content");
        put(ResetContent, "Reset Content");
        put(PartialContent, "Partial Content");
        put(MultipleChoices, "Multiple Choices");
        put(MovedPermanently, "Moved Permanently");
        put(Found, "Found");
        put(SeeOther, "See Other");
        put(NotModified, "Not Modified");
        put(UseProxy, "Use Proxy");
        put(TemporaryRedirect, "Temporary Redirect");
        put(BadRequest, "Bad Request");
        put(Unauthorized, "Unauthorized");
        put(PaymentRequired, "Payment Required");
        put(Forbidden, "Forbidden");
        put(NotFound, "Not Found");
        put(MethodNotAllowed, "Method Not Allowed");
        put(NotAcceptable, "Not Acceptable");
        put(ProxyAuthenticationRequired, "Proxy Authentication Required");
        put(RequestTimeout, "Request Timeout");
        put(Conflict, "Conflict");
        put(Gone, "Gone");
        put(LengthRequired, "Length Required");
        put(PreconditionFailed, "Precondition Failed");
        put(PayloadTooLarge, "Payload Too Large");
        put(URITooLong, "URI Too Long");
        put(UnsupportedMediaType, "Unsupported Media Type");
        put(RangeNotSatisfiable, "Range Not Satisfiable");
        put(ExpectationFailed, "Expectation Failed");
        put(UpgradeRequired, "Upgrade Required");
        put(InternalServerError, "Internal Server Error");
        put(NotImplemented, "Not Implemented");
        put(BadGateway, "Bad Gateway");
        put(ServiceUnavailable, "Service Unavailable");
        put(GatewayTimeout, "Gateway Timeout");
        put(HTTPVersionNotSupported, "HTTP Version Not Supported");
    }};

    public static String description(int statusCode) {
        return statusCodeDescriptions.get(statusCode);
    }

    private HttpStatusCode() { }
}
