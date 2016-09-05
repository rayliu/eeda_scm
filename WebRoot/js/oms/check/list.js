
$(document).ready(function() {
	document.title = '波次复核查询   | '+document.title;

    $('#menu_incoming').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = eeda.dt({
        "id": "eeda-table",
        "ajax": "/check/list?flag=wave",
        "columns": [
			{ "width": "30px",
			    "render": function ( data, type, full, meta ) {
			    	return meta.row+1;
			    }
			},
            { "data": "GATE_OUT_NO"}, 
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
            { "data": "WAVE_ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/waveOrder/edit?id="+full.WAVE_ID+"' >"+data+"</a>";
                }
            },
            { "data": "CARGO_BAR_CODE"},
            { "data": "CARGO_NAME"}, 
            { "data": "AMOUNT"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#waveCheckForm")[0].reset();
    });
    
    
    $('#waveCheckForm').on('keydown','input',function(e){
    	var key = e.which;
    	if(key == 13){
            var id = $(this).attr('id');
            if(id == 'wave_order_no'){
                searchData();
                $('#cargo_bar_code').focus();
            }else{
                var wave_order_no = $('#wave_order_no').val().trim();
                var cargo_bar_code = $('#cargo_bar_code').val().trim();
                if(wave_order_no!='' && cargo_bar_code!=''){
                    checkItem(wave_order_no,cargo_bar_code);
                }    
            }
    	}

    })
    
    var checkItem = function(wave_order_no , cargo_bar_code){
    	$.post('/check/checkOrder',{wave_order_no:wave_order_no, cargo_bar_code:cargo_bar_code},function(data){
    		var order = data;
            if(order.MSG =='success'){
                $.scojs_message('复核成功', $.scojs_message.TYPE_OK);
                searchData();
                $('#cargo_bar_code').val('');
                $('#cargo_bar_code').focus();
            }else{
                $.scojs_message(order.MSG, $.scojs_message.TYPE_ERROR);
            }
    	}).fail(function() {
            $.scojs_message('复核失败,请联系管理员', $.scojs_message.TYPE_ERROR);
       });
    }

    var buildCondition=function(){
    	var item = {};
    	var orderForm = $('#waveCheckForm input,select');
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
    	var url = "/check/list?jsonStr="+JSON.stringify(itemJson)+"&flag=wave";
        dataTable.ajax.url(url).load();
    };
    
});