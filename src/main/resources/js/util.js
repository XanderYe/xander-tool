;(function(){
  const util = {
    // date对象格式化成字符串
    formatDate: function (date, format) {
      if (!date) {
        date = new Date();
      }
      if (!format) {
        format = "yyyy-MM-dd HH:mm:ss";
      }
      const dateReg = {
        "M+": date.getMonth() + 1,
        "d+": date.getDate(),
        "H+": date.getHours(),
        "h+": date.getHours() > 12 ? date.getHours() - 12 : date.getHours(),
        "m+": date.getMinutes(),
        "s+": date.getSeconds(),
        "q+": Math.floor(date.getMonth() / 3 + 1),
        "S": date.getMilliseconds()
      }
      let dateString = format;
      if (/(y+)/.test(dateString)) {
        dateString = dateString.replace(RegExp.$1, (date.getFullYear() + "").substr(4 - RegExp.$1.length));
      }
      for (let reg in dateReg) {
        if (new RegExp("(" + reg + ")").test(dateString)) {
          dateString = dateString.replace(RegExp.$1, (RegExp.$1.length === 1) ? (dateReg[reg]) : (("00" + dateReg[reg]).substr(("" + dateReg[reg]).length)));
        }
      }
      return dateString;
    },

    // 时间字符串转date对象
    parseDate: function (dateString) {
      return new Date(dateString.replace(/-/,"/"));
    },

    // 获取url地址参数
    getUrlParam: function (name) {
      const reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
      const r = window.location.search.substr(1).match(reg);
      if (r != null) {
        return unescape(r[2]);
      }
      return null;
    }
  }

  if (typeof module !== 'undefined' && typeof exports === 'object') {
    module.exports = util;
  } else if (typeof define === 'function' && (define.amd || define.cmd)) {
    define(function() {
      return util;
    });
  } else {
    this.util = util;
  }
}).call(this || (typeof window !== 'undefined' ? window : global));
