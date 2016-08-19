
$(document).ready(function() {
    $('#payBtn').click(function(){
    	var datetime = $('#create_stamp').val();
    	if(datetime.indexOf('.') != -1){
    		datetime = datetime.substring(0,datetime.indexOf('.')).replace(/\:/g,"").replace(/\-/g,'').replace(' ','');
    	}else{
    		datetime = datetime.replace(/\:/g,"").replace(/\-/g,'').replace(' ','');
    	}
    	
    	var item = salesOrder.buildCargoDetail();
    	
        var allinpayServer = salesOrder.allinpayServer;
        var callbackServer = salesOrder.allinpayCallbackServer;
        var formObj = {
            serverUrl : "http://"+allinpayServer+"/gateway/index.do?",
            inputCharset : '1', //字符集
            pickupUrl: 'http://'+callbackServer+'/allinpay/pickupUrl', //回调页面,  即取货地址
            receiveUrl: 'http://'+callbackServer+'/allinpay/payResultRecv', //后台回调, 即商户系统通知地址
            version: 'v1.0', //版本号
            language: '1', //语言,  1代表utf-8
            signType: '1',//签名类型
            merchantId: salesOrder.merchantId, //商户号
            //payerName: '飞龙',//付款人姓名
            payerName: $('#payer_name').val(),//付款人姓名
            payerEmail: $('#payer_email').val(), //付款人联系email
            payerTelephone: $('#payer_phone').val(), //付款人电话
            
            //payerIDCard: '', //付款人证件号
            payerIDCard: $('#payer_account').val(),
            pid: '', //合作伙伴商户号
            orderNo: $('#order_no').val(),//商户系统订单号
            orderAmount: parseFloat($('#goods_value').val())*100,//订单金额(单位分)
            //orderCurrency: '0', //订单金额币种类型: 0, 156 人民币；  344 港币； 840 美元
            orderCurrency: $('#pay_currency').val(), //订单金额币种类型: 0, 156 人民币；  344 港币； 840 美元
            //orderDatetime: '20160622103144',//商户的订单提交时间
            orderDatetime: datetime,//商户的订单提交时间
            orderExpireDatetime: '60', //订单过期时间
            //productName: '火星人', //商品名称
            productName: item[0].item_name, //商品名称
            //productPrice: '9999',//商品单价
            productPrice: item[0].price*100,//商品单价
            //productNum: '1', //商品数量
            productNum: item[0].qty, //商品数量
            //productId: 'Mars man', //商品标识
            productId: item[0].item_no, //商品标识
            //productId: item[0].item_no, //商品标识
            productDesc: item[0].item_desc,//商品描述
            ext1: '123',//扩展字段1
            ext2: '123',//扩展字段2
            extTL: '',//业务扩展字段
            payType: '0', //支付方式
            issuerId: '', //发卡方代码
            pan: '', //付款人支付卡号
            tradeNature: 'GOODS', //贸易类型:  GOODS表示实物类型，SERVICES表示服务类型
            key: '1234567890', //key, 用于计算signMsg的key值
        }

        
        $('#payForm [name=serverUrl]').val(formObj.serverUrl);
        $('#payForm [name=inputCharset]').val(formObj.inputCharset);
        $('#payForm [name=pickupUrl]').val(formObj.pickupUrl);
        $('#payForm [name=receiveUrl]').val(formObj.receiveUrl);
        $('#payForm [name=version]').val(formObj.version);
        $('#payForm [name=language]').val(formObj.language);
        $('#payForm [name=signType]').val(formObj.signType);
        $('#payForm [name=merchantId]').val(formObj.merchantId);
        $('#payForm [name=payerName]').val(formObj.payerName);
        $('#payForm [name=payerEmail]').val(formObj.payerEmail);
        $('#payForm [name=payerTelephone]').val(formObj.payerTelephone);
        $('#payForm [name=payerIDCard]').val(formObj.payerIDCard);
        $('#payForm [name=pid]').val(formObj.pid);
        $('#payForm [name=orderNo]').val(formObj.orderNo);
        $('#payForm [name=orderAmount]').val(formObj.orderAmount);
        $('#payForm [name=orderCurrency]').val(formObj.orderCurrency);
        $('#payForm [name=orderDatetime]').val(formObj.orderDatetime);
        $('#payForm [name=orderExpireDatetime]').val(formObj.orderExpireDatetime);
        $('#payForm [name=productName]').val(formObj.productName);
        $('#payForm [name=productPrice]').val(formObj.productPrice);
        $('#payForm [name=productNum]').val(formObj.productNum);
        $('#payForm [name=productId]').val(formObj.productId);
        $('#payForm [name=productDesc]').val(formObj.productDesc);
        $('#payForm [name=ext1]').val(formObj.ext1);
        $('#payForm [name=ext2]').val(formObj.ext2);
        $('#payForm [name=extTL]').val(formObj.extTL);
        $('#payForm [name=payType]').val(formObj.payType);
        $('#payForm [name=issuerId]').val(formObj.issuerId);
        $('#payForm [name=pan]').val(formObj.pan);
        $('#payForm [name=tradeNature]').val(formObj.tradeNature);
        $('#payForm [name=key]').val(formObj.key);

        $('#payForm').submit();
    });

} );