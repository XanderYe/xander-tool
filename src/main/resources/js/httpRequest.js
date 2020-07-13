var http = require("http");
var https = require("https");
var querystring = require('querystring');

var defaultUserAgent = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36";

var options = {
    hostname: "",
    port: 80,
    path: "",
    method: "GET",
    headers: {}
}

const get = (url, headers, params) => {
    if (!headers) {
        headers = {};
    }
    if (!(headers['user-agent'] || headers['User-Agent'])) {
        headers['User-Agent'] = defaultUserAgent;
    }
    const opt = getOpt(url);
    options = {
        hostname: opt.hostname,
        port: opt.port,
        path: opt.path + "?" + querystring.stringify(params),
        method: "GET",
        headers: headers
    }
    const httpRequest = opt.httpRequest;
    return new Promise((resolve, reject) => {
        let req = httpRequest.request(options, (res) => {
            res.setEncoding("utf-8");
            if (res.statusCode === 200) {
                res.on("data", (data) => {
                    resolve(data);
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

const post = (url, headers, params) => {
    if (!headers) {
        headers = {
            "User-Agent": defaultUserAgent
        };
    } else if (!(headers['user-agent'] || headers['User-Agent'])) {
        headers['User-Agent'] = defaultUserAgent;
    }
    headers['Content-Type'] = "application/x-www-form-urlencoded";
    const opt = getOpt(url);
    options = {
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
                    resolve(data);
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

const postJSON = (url, headers, params) => {
    if (!headers) {
        headers = {
            "User-Agent": defaultUserAgent
        };
    } else if (!(headers['user-agent'] || headers['User-Agent'])) {
        headers['User-Agent'] = defaultUserAgent;
    }
    headers['Content-Type'] = "application/json";
    const opt = getOpt(url);
    options = {
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
            if (res.statusCode == 200) {
                res.on("data", (data) => {
                    resolve(data);
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

var getOpt = (url) => {
    let splits = url.split("/");
    if (splits.length < 4) {
        throw "url错误，请检查";
    }
    let protocol = splits[0].substring(0, splits[0].length - 1);
    let ssl = protocol === "https";
    let host = splits[2];
    hostname = host.split(":")[0];
    port = host.split(":").length > 1 ? host.split(":")[1] : ssl ? 443 : 80;
    path = "/" + url.substring(url.indexOf(splits[3]));
    return {
        ssl: ssl,
        httpRequest: ssl ? https : http,
        hostname: hostname,
        port: parseInt(port),
        path: path
    }
}

module.exports = {
    get: get,
    post: post,
    postJSON: postJSON
};