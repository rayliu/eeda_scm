
$(document).ready(function() {
	document.title = '波次复核查询   | '+document.title;

    $('#menu_incoming').addClass('active').find('ul').addClass('in');

	  //datatable, 动态处理
    var orderDataTable = eeda.dt({
        "id": "order-table",
        "ajax": "/check/list?flag=order",
        "columns": [
			{ "width": "30px",
			    "render": function ( data, type, full, meta ) {
			    	return meta.row+1;
			    }
			},
            { "data": "GATE_OUT_NO", 
                "render": function ( data, type, full, meta ) {
                	if(full.GATE_OUT_ID)
                		return "<a href='/gateOutOrder/edit?id="+full.GATE_OUT_ID+"' >"+data+"</a>";
                	else
                		return data;
                }
            },
            { "data": null},
            { "data": "PICKUP_FLAG",
            	"render": function ( data, type, full, meta ) {
                    var pickup_flag = data;
                    var status = '';
                    if(pickup_flag == 'N'){
                    	status = '未拣货';
                    }else{
                    	status = '已拣货';
                    }
                    return status;
                }
            },
            { "data": "CREATE_STAMP"},
            { "data": "WAVE_ORDER_NO"},
            { "data": "CARGO_BAR_CODE"},
            { "data": "CARGO_NAME"}, 
            { "data": "AMOUNT"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#orderCheckForm")[0].reset();
    });

    $('#gate_out_order_no').keydown(function(e){
    	var key = e.which;
    	if(key == 13){
			var gate_out_order_no = $(this).val().trim();
			if(gate_out_order_no!=''){
				checkItem(gate_out_order_no);
			}
    	}
    })
    
    var checkItem = function(gate_out_order_no){
    	$.post('/check/checkOrder',{gate_out_order_no:gate_out_order_no},function(data){
    		var order = data;
            if(order.MSG =='success'){
                $.scojs_message('复核成功', $.scojs_message.TYPE_OK);
                searchData();
                $('#gate_out_order_no').val('');
                $('#gate_out_order_no').focus();
            }else{
                $.scojs_message('复核失败', $.scojs_message.TYPE_ERROR);
            }
    	}).fail(function() {
            $.scojs_message('复核失败,请联系管理员', $.scojs_message.TYPE_ERROR);
       });
    }

    var buildCondition=function(){
    	var item = {};
    	var orderForm = $('#orderCheckForm input,select');
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
    	var url = "/check/list?jsonStr="+JSON.stringify(itemJson)+"&flag=order";
    	orderDataTable.ajax.url(url).load();
    };
    
});