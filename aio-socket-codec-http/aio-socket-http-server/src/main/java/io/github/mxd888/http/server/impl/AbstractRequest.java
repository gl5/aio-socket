package io.github.mxd888.http.server.impl;

import io.github.mxd888.http.common.Cookie;
import io.github.mxd888.http.common.Reset;
import io.github.mxd888.http.server.HttpRequest;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @author MDong
 * @version 2.10.1.v20211002-RELEASE
 */
abstract class AbstractRequest implements HttpRequest, Reset {

    protected Request request;

    protected void init(Request request) {
        this.request = request;
    }


    @Override
    public final String getHeader(String headName) {
        return request.getHeader(headName);
    }

    @Override
    public final Collection<String> getHeaders(String name) {
        return request.getHeaders(name);
    }

    @Override
    public final Collection<String> getHeaderNames() {
        return request.getHeaderNames();
    }


    @Override
    public final String getRequestURI() {
        return request.getRequestURI();
    }

    @Override
    public final String getProtocol() {
        return request.getProtocol();
    }

    @Override
    public final String getMethod() {
        return request.getMethod();
    }

    @Override
    public final String getScheme() {
        return request.getScheme();
    }

    @Override
    public final String getRequestURL() {
        return request.getRequestURL();
    }

    @Override
    public final String getQueryString() {
        return request.getQueryString();
    }

    @Override
    public final String getContentType() {
        return request.getContentType();
    }

    @Override
    public final int getContentLength() {
        return request.getContentLength();
    }

    @Override
    public final String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public final Map<String, String[]> getParameters() {
        return request.getParameters();
    }

    @Override
    public final String[] getParameterValues(String name) {
        return request.getParameters().get(name);
    }

    @Override
    public final String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public final InetSocketAddress getRemoteAddress() {
        return request.getRemoteAddress();
    }

    @Override
    public final InetSocketAddress getLocalAddress() {
        return request.getLocalAddress();
    }

    @Override
    public final String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public final Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public final Enumeration<Locale> getLocales() {
        return request.getLocales();
    }

    @Override
    public final String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    public final Request getRequest() {
        return request;
    }

    @Override
    public Cookie[] getCookies() {
        return request.getCookies();
    }

    @Override
    public <A> A getAttachment() {
        return request.getAttachment();
    }

    @Override
    public <A> void setAttachment(A attachment) {
        request.setAttachment(attachment);
    }

    public abstract AbstractResponse getResponse();
}
