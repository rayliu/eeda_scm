
$(document).ready(function() {
	document.title = '订单查询   | '+document.title;
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/salesOrder/list",
        "scrollX": true,
        "columns": [
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/salesOrder/edit?id="+full.ID+"'>"+data+"</a>";
                }
            },
            { "data": "GOODS_INFO"},
            { "data": "CONSIGNEE"}, 
            { "data": "ACTURAL_PAID"}, 
            { "data": "STATUS",
            	"render": function ( data, type, full, meta ) {
            		var msg = '';
            		if(data == '已通关'){
            			msg = "<span style='color:green'>"+data+"</span>";
            		}else if(data=='报关处理中'){
            			msg = "<span style='color:red'>"+data+"</span>";
            		}else{
            			msg = data;
            		}
            		return msg;
                }
            }, 
            { 	
            	"render": function ( data, type, full, meta ) {
            		if(full.PAY_NO != null && full.PAY_NO != ''){
            			return '已支付';
            		}else{
            			return '未支付';
            		}
            	}
            }, 
            
            { "data": "CEB_REPORT",
            	"render": function ( data, type, full, meta ) {
            		return showType(data);
            	}
            }, 
            { "data": "RETURN_STATUS",
            	"render": function ( data, type, full, meta ) {
            		var type = full.CEB_REPORT;
            		return showStatus(type,data);
            	}	
            }, 
            { "data": "RETURN_INFO"}, 
            { "data": "RETURN_TIME"}, 
            { "data": "PAY_STATUS",
            	"render": function ( data, type, full, meta ) {
            		var msg = '';
            		if(data == null){
            			if(full.PAY_NO != null && full.PAY_NO != ''){
            				msg = '<span style="color:blue">已支付，未申报</span>';
            			}else{
            				msg = '未付款';
            			}
            		}else if(data=='接收成功'){
            			msg = "<span style='color:green'>付款已完成</span>";
            		}else{
            			msg = "<span style='color:red'>"+data+"</span>";
            		}
            		return msg;
                }
            }, 
            {"data": "LOG_STATUS",
            	"render": function ( data, type, full, meta ) {
	        		var msg = '';
	        		if(data==null){
	        			msg = '运单不存在';
	        		}else if(data=='已通关'){
	        			msg = "<span style='color:green'>"+data+"</span>";
	        		}else if(data=='报关处理中'){
	        			msg = "<span style='color:red'>"+data+"</span>";
	        		}else{
	        			msg = data;
	        		}
	        		return msg;
                }
            }, 
            {"data": "BILL_CUS_STATUS",
            	"render": function ( data, type, full, meta ) {
	        		var msg = '';
	        		if(data==null){
	        			msg = '清单不存在';
	        		}else if(data=='审批通过'){
	        			msg = "<span style='color:green'>"+data+"</span>";
	        		}else {
	        			msg = "<span style='color:red'>"+data+"</span>";
	        		}
	        		return msg;
                }
            }, 
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#orderForm")[0].reset();
    });

    $('#searchBtn').click(function(){
        searchData(); 
    })

    buildCondition=function(){
    	var item = {};
    	var orderForm = $('#orderForm input,select');
    	for(var i = 0; i < orderForm.length; i++){
    		var name = orderForm[i].id;
        	var value =orderForm[i].value;
        	if(name){
        		item[name] = value;
        	}
    	}
        return item;
    };

    var searchData=function(){
    	var itemJson = buildCondition();
    	var url = "/salesOrder/list?jsonStr="+JSON.stringify(itemJson);
        dataTable.ajax.url(url).load();
    };
    
    $('#downloadBtn').click(function(e){
        var itemJson = buildCondition();
        var url = "/salesOrder/downloadList?jsonStr="+JSON.stringify(itemJson);
        $.post(url, function(data){
            if(data)
                window.open(data);
        });
    });
    
    var showType = function(value){
    	if(value=='ceb312'){
    		value= '（海关）订单回执';
    	}else if(value=='ceb512'){
    		value= '（海关）运单回执';
    	}else if(value=='ceb514'){
    		value= '（海关）运单回执状态（签收单）回执';
    	}else if(value=='ceb622'){
    		value= '（海关）清单回执';
    	}else if(value=='ceb624'){
    		value= '（海关）撤销单回执';
    	}else if(value=='ceb816'){
    		value= '（海关）税单回执';
    	}else if(value=='ceb712'){
    		value= '（海关）入库明细回执';
    	}else if(value=='ceb901'){
    		value= '（海关）核放单';
    	}else if(value=='ceb301'){  //国检
    		value= '（国检）订单回执';
    	}else if(value=='ceb501'){  //国检
    		value= '（国检）运单回执';
    	}else if(value=='ceb801'){  //国检
    		value= '（国检）出区申报单回执';
    	}else if(value=='ceb902'){  //国检
    		value= '（国检）核放单';
    	}
    	return value;
    }
    
    var showStatus = function(type,value){
    	if(type=='ceb301' || type=='ceb501' ||type=='ceb801' ||type=='ceb902'){
    		if(value == '30'){
        		value = '已接收(0)';
        	}else if(value == '31'){
        		value = '备案通过（1）';
        	}else if(value == '32'){
        		value = '备案不通过（2）';
        	}else if(value == '52'){
        		value = '接受失败（4）';
        	}
    	}else{
    		if(value == '23'){
        		value = '发送海关成功（3）';
        	}else if(value == '24'){
        		value = '发送海关失败（4）';
        	}else if(value == '30'){
        		value = '海关入库（120）';
        	}else if(value == '32'){
        		value = '海关退单（100）';
        	}else if(value == '31'){
        		value = '海关审结（399）';
        	}else if(value == '34'){
        		value = '海关放行（800）';
        	}else if(value == '39'){
        		value = '海关结关（899）';
        	}else if(value == '-1'){
        		value = '处理异常';
        	}
    	}
    	return value;
    }
    
   
    
});