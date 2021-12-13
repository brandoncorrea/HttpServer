package httpServerTest;

import httpServer.HttpStatusCode;
import org.junit.Assert;
import org.junit.Test;

public class HttpStatusCodeTest {
    @Test
    public void httpStatusCodeValues() {
        Assert.assertEquals(100, HttpStatusCode.Continue);
        Assert.assertEquals(101, HttpStatusCode.SwitchingProtocols);
        Assert.assertEquals(200, HttpStatusCode.OK);
        Assert.assertEquals(201, HttpStatusCode.Created);
        Assert.assertEquals(202, HttpStatusCode.Accepted);
        Assert.assertEquals(203, HttpStatusCode.NonAuthoritativeInformation);
        Assert.assertEquals(204, HttpStatusCode.NoContent);
        Assert.assertEquals(205, HttpStatusCode.ResetContent);
        Assert.assertEquals(206, HttpStatusCode.PartialContent);
        Assert.assertEquals(300, HttpStatusCode.MultipleChoices);
        Assert.assertEquals(301, HttpStatusCode.MovedPermanently);
        Assert.assertEquals(302, HttpStatusCode.Found);
        Assert.assertEquals(303, HttpStatusCode.SeeOther);
        Assert.assertEquals(304, HttpStatusCode.NotModified);
        Assert.assertEquals(305, HttpStatusCode.UseProxy);
        Assert.assertEquals(307, HttpStatusCode.TemporaryRedirect);
        Assert.assertEquals(400, HttpStatusCode.BadRequest);
        Assert.assertEquals(401, HttpStatusCode.Unauthorized);
        Assert.assertEquals(402, HttpStatusCode.PaymentRequired);
        Assert.assertEquals(403, HttpStatusCode.Forbidden);
        Assert.assertEquals(404, HttpStatusCode.NotFound);
        Assert.assertEquals(405, HttpStatusCode.MethodNotAllowed);
        Assert.assertEquals(406, HttpStatusCode.NotAcceptable);
        Assert.assertEquals(407, HttpStatusCode.ProxyAuthenticationRequired);
        Assert.assertEquals(408, HttpStatusCode.RequestTimeout);
        Assert.assertEquals(409, HttpStatusCode.Conflict);
        Assert.assertEquals(410, HttpStatusCode.Gone);
        Assert.assertEquals(411, HttpStatusCode.LengthRequired);
        Assert.assertEquals(412, HttpStatusCode.PreconditionFailed);
        Assert.assertEquals(413, HttpStatusCode.PayloadTooLarge);
        Assert.assertEquals(414, HttpStatusCode.URITooLong);
        Assert.assertEquals(415, HttpStatusCode.UnsupportedMediaType);
        Assert.assertEquals(416, HttpStatusCode.RangeNotSatisfiable);
        Assert.assertEquals(417, HttpStatusCode.ExpectationFailed);
        Assert.assertEquals(426, HttpStatusCode.UpgradeRequired);
        Assert.assertEquals(500, HttpStatusCode.InternalServerError);
        Assert.assertEquals(501, HttpStatusCode.NotImplemented);
        Assert.assertEquals(502, HttpStatusCode.BadGateway);
        Assert.assertEquals(503, HttpStatusCode.ServiceUnavailable);
        Assert.assertEquals(504, HttpStatusCode.GatewayTimeout);
        Assert.assertEquals(505, HttpStatusCode.HTTPVersionNotSupported);
    }

    @Test
    public void statusCodeDescription() {
        Assert.assertEquals("Continue", HttpStatusCode.description(HttpStatusCode.Continue));
        Assert.assertEquals("Switching Protocols", HttpStatusCode.description(HttpStatusCode.SwitchingProtocols));
        Assert.assertEquals("OK", HttpStatusCode.description(HttpStatusCode.OK));
        Assert.assertEquals("Accepted", HttpStatusCode.description(HttpStatusCode.Accepted));
        Assert.assertEquals("Non-Authoritative Information", HttpStatusCode.description(HttpStatusCode.NonAuthoritativeInformation));
        Assert.assertEquals("No Content", HttpStatusCode.description(HttpStatusCode.NoContent));
        Assert.assertEquals("Reset Content", HttpStatusCode.description(HttpStatusCode.ResetContent));
        Assert.assertEquals("Partial Content", HttpStatusCode.description(HttpStatusCode.PartialContent));
        Assert.assertEquals("Multiple Choices", HttpStatusCode.description(HttpStatusCode.MultipleChoices));
        Assert.assertEquals("Moved Permanently", HttpStatusCode.description(HttpStatusCode.MovedPermanently));
        Assert.assertEquals("Found", HttpStatusCode.description(HttpStatusCode.Found));
        Assert.assertEquals("See Other", HttpStatusCode.description(HttpStatusCode.SeeOther));
        Assert.assertEquals("Not Modified", HttpStatusCode.description(HttpStatusCode.NotModified));
        Assert.assertEquals("Use Proxy", HttpStatusCode.description(HttpStatusCode.UseProxy));
        Assert.assertEquals("Temporary Redirect", HttpStatusCode.description(HttpStatusCode.TemporaryRedirect));
        Assert.assertEquals("Bad Request", HttpStatusCode.description(HttpStatusCode.BadRequest));
        Assert.assertEquals("Unauthorized", HttpStatusCode.description(HttpStatusCode.Unauthorized));
        Assert.assertEquals("Payment Required", HttpStatusCode.description(HttpStatusCode.PaymentRequired));
        Assert.assertEquals("Forbidden", HttpStatusCode.description(HttpStatusCode.Forbidden));
        Assert.assertEquals("Not Found", HttpStatusCode.description(HttpStatusCode.NotFound));
        Assert.assertEquals("Method Not Allowed", HttpStatusCode.description(HttpStatusCode.MethodNotAllowed));
        Assert.assertEquals("Not Acceptable", HttpStatusCode.description(HttpStatusCode.NotAcceptable));
        Assert.assertEquals("Proxy Authentication Required", HttpStatusCode.description(HttpStatusCode.ProxyAuthenticationRequired));
        Assert.assertEquals("Request Timeout", HttpStatusCode.description(HttpStatusCode.RequestTimeout));
        Assert.assertEquals("Conflict", HttpStatusCode.description(HttpStatusCode.Conflict));
        Assert.assertEquals("Gone", HttpStatusCode.description(HttpStatusCode.Gone));
        Assert.assertEquals("Length Required", HttpStatusCode.description(HttpStatusCode.LengthRequired));
        Assert.assertEquals("Precondition Failed", HttpStatusCode.description(HttpStatusCode.PreconditionFailed));
        Assert.assertEquals("Payload Too Large", HttpStatusCode.description(HttpStatusCode.PayloadTooLarge));
        Assert.assertEquals("URI Too Long", HttpStatusCode.description(HttpStatusCode.URITooLong));
        Assert.assertEquals("Unsupported Media Type", HttpStatusCode.description(HttpStatusCode.UnsupportedMediaType));
        Assert.assertEquals("Range Not Satisfiable", HttpStatusCode.description(HttpStatusCode.RangeNotSatisfiable));
        Assert.assertEquals("Expectation Failed", HttpStatusCode.description(HttpStatusCode.ExpectationFailed));
        Assert.assertEquals("Upgrade Required", HttpStatusCode.description(HttpStatusCode.UpgradeRequired));
        Assert.assertEquals("Internal Server Error", HttpStatusCode.description(HttpStatusCode.InternalServerError));
        Assert.assertEquals("Not Implemented", HttpStatusCode.description(HttpStatusCode.NotImplemented));
        Assert.assertEquals("Bad Gateway", HttpStatusCode.description(HttpStatusCode.BadGateway));
        Assert.assertEquals("Service Unavailable", HttpStatusCode.description(HttpStatusCode.ServiceUnavailable));
        Assert.assertEquals("Gateway Timeout", HttpStatusCode.description(HttpStatusCode.GatewayTimeout));
        Assert.assertEquals("HTTP Version Not Supported", HttpStatusCode.description(HttpStatusCode.HTTPVersionNotSupported));

    }

}
