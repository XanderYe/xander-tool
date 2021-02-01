const http = require("http");
const https = require("https");
const querystring = require('querystring');

const defaultUserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

const get = (url, params, headers, cookies) => {
    if (!headers) {
        headers = {};
    }
    if (!(headers['user-agent'] || headers['User-Agent'])) {
        headers['User-Agent'] = defaultUserAgent;
    }
    if (cookies) {
        headers['Cookie'] = getCookieString(cookies);
    }
    const opt = getOpt(url);
    const options = {
        hostname: opt.hostname,
        port: opt.port,
        path: params ? opt.path + "?" + querystring.stringify(params) : opt.path,
        method: "GET",
        headers: headers
    }
    const httpRequest = opt.httpRequest;
    return new Promise((resolve, reject) => {
        let req = httpRequest.request(options, (res) => {
            res.setEncoding("utf-8");
            if (res.statusCode === 200) {
                res.on("data", (data) => {
                    let cookiesArr = res.headers['set-cookie'];
                    cookies = formatCookies(cookiesArr);
                    resolve([data, cookies]);
                });
            } else {
                reject(res.statusCode);
            }
        });
        req.on("error", (err) => {
            reject(err.message);
        });
        req.end();
    })
}

const post = (url, params, headers, cookies) => {
    if (!headers) {
        headers = {
            "User-Agent": defaultUserAgent
        };
    } else if (!(headers['user-agent'] || headers['User-Agent'])) {
        headers['User-Agent'] = defaultUserAgent;
    }
    headers['Content-Type'] = "application/x-www-form-urlencoded";
    if (cookies) {
        headers['Cookie'] = getCookieString(cookies);
    }
    const opt = getOpt(url);
    const options = {
        hostname: opt.hostname,
        port: opt.port,
        path: opt.path,
        method: "POST",
        headers: headers
    }
    const httpRequest = opt.httpRequest;
    return new Promise((resolve, reject) => {
        let req = httpRequest.request(options, (res) => {
            res.setEncoding("utf-8");
            if (res.statusCode === 200) {
                res.on("data", (data) => {
                    let cookiesArr = res.headers['set-cookie'];
                    cookies = formatCookies(cookiesArr);
                    resolve([data, cookies]);
                });
            } else {
                reject(res.statusCode);
            }
        });
        req.on("error", (err) => {
            reject(err.message);
        });
        req.write(querystring.stringify(params));
        req.end();
    })
}

const postJSON = (url, params, headers, cookies) => {
    if (!headers) {
        headers = {
            "User-Agent": defaultUserAgent
        };
    } else if (!(headers['user-agent'] || headers['User-Agent'])) {
        headers['User-Agent'] = defaultUserAgent;
    }
    headers['Content-Type'] = "application/json";
    if (cookies) {
        headers['Cookie'] = getCookieString(cookies);
    }
    const opt = getOpt(url);
    const options = {
        hostname: opt.hostname,
        port: opt.port,
        path: opt.path,
        method: "POST",
        headers: headers
    }
    const httpRequest = opt.httpRequest;
    return new Promise((resolve, reject) => {
        let req = httpRequest.request(options, (res) => {
            res.setEncoding("utf-8");
            if (res.statusCode === 200) {
                res.on("data", (data) => {
                    let cookiesArr = res.headers['set-cookie'];
                    cookies = formatCookies(cookiesArr);
                    resolve([data, cookies]);
                });
            } else {
                reject(res.statusCode);
            }
        });
        req.on("error", (err) => {
            reject(err.message);
        });
        req.write(JSON.stringify(params));
        req.end();
    })
}

const getOpt = (url) => {
    let splits = url.split("/");
    if (splits.length < 4) {
        throw "url format error";
    }
    let protocol = splits[0].substring(0, splits[0].length - 1);
    let ssl = protocol === "https";
    let host = splits[2];
    let hostname = host.split(":")[0];
    let port = host.split(":").length > 1 ? host.split(":")[1] : ssl ? 443 : 80;
    let path = "/" + url.substring(url.indexOf(splits[3]));
    return {
        ssl: ssl,
        httpRequest: ssl ? https : http,
        hostname: hostname,
        port: parseInt(port),
        path: path
    }
}

const getCookieString = (cookies) => {
    let cookieString = "";
    for(const key in cookies){
        cookieString += key + "=" + cookies[key] + ";";
    }
    return cookieString;
}

const formatCookies = (cookiesArr) => {
    let cookieJSON = {};
    for (const i in cookiesArr) {
        let cookie = cookiesArr[i];
        if (cookie) {
            const ss = cookie.substring(0, cookie.indexOf(";")).split("=");
            const key = ss[0] ? ss[0].trim() : ss[0];
            const value = ss[1] ? ss[1].trim() : ss[1];
            if (key) {
                cookieJSON[key] = value;
            }
        }
    }
    return cookieJSON;
}

module.exports = {
    get: get,
    post: post,
    postJSON: postJSON
};
