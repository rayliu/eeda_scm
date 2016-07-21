
$(document).ready(function() {
	document.title = '单据状态查询查询   | '+document.title;

    $('#menu_customs').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = $('#eeda-table').DataTable({
        "processing": true,
        "searching": false,
        //"serverSide": true,
        "scrollX": true,
        "scrollY": "300px",
        "scrollCollapse": true,
        "autoWidth": false,
        "language": {
            "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
        },
        "ajax": "/gateInOrder/list",
        "columns": [
            { "data": "ORDER_NO", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/gateInOrder/edit?id="+full.ID+"'target='_blank'>"+data+"</a>";
                }
            },
            { "data": "WAREHOUSE_NAME"},
            { "data": "ORDER_TYPE"}, 
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}, 
            { "data": "STATUS"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#orderForm")[0].reset();
    });

    $('#searchBtn').click(function(){
        //searchData(); 
    	var sales_order_no = $('#sales_order_no').val();
    	$.post('/searchStatus/search',{sales_order_no:sales_order_no},function(data){
    		if(data){
    			//$('#code').val(data.code);
    			//$('#massage').val(data.massage);
    			var bills = data.bills[0];
    			$('#bill_ciq_no').val(bills.bill_ciq_no);
    			$('#bill_ciq_result').val(bills.bill_ciq_result);
    			$('#bill_ciq_status').val(statusShow(bills.bill_ciq_status));
    			$('#bill_ciq_time').val(bills.bill_ciq_time);
    			$('#bill_cus_no').val(bills.bill_cus_no);
    			$('#bill_cus_result').val(bills.bill_cus_result);
    			$('#bill_cus_status').val(statusShow(bills.bill_cus_status));
    			$('#bill_cus_time').val(bills.bill_cus_time);
    			$('#code').val(bills.code);
    			$('#logistics_ciq_result').val(bills.logistics_ciq_result);
    			$('#logistics_ciq_status').val(statusShow(bills.logistics_ciq_status));
    			$('#logistics_cus_result').val(bills.logistics_cus_result);
    			$('#logistics_cus_status').val(statusShow(bills.logistics_cus_status));
    			$('#logistics_no').val(bills.logistics_no);
    			$('#message').val(bills.message);
    			$('#order_ciq_result').val(bills.order_ciq_result);
    			$('#order_ciq_status').val(statusShow(bills.order_ciq_status));
    			$('#order_cus_result').val(bills.order_cus_result);
    			$('#order_cus_status').val(statusShow(bills.order_cus_status));
    			$('#ordr_no').val(bills.ordr_no);
    			$('#pay_no').val(bills.pay_no);
    			$('#pay_result').val(bills.pay_result);
    			$('#pay_status').val(statusShow(bills.pay_status));
    		}
    	})
    })
    
    var statusShow = function(status){
    	var massage = '';
    	if(status==10){
    		massage = '未发送';
    	}else if(status==20){
    		massage = '已发送';
    	}else if(status==30){
    		massage = '接收成功';
    	}else if(status==32){
    		massage = '接收不成功';
    	}
    	return massage;
    }

    

   var searchData=function(){
        var order_no = $("#order_no").val(); 
        var status = $('#status').val();
        var start_date = $("#create_stamp_begin_time").val();
        var end_date = $("#create_stamp_end_time").val();
        
        /*  
            查询规则：参数对应DB字段名
            *_no like
            *_id =
            *_status =
            时间字段需成双定义  *_begin_time *_end_time   between
        */
        var url = "/gateInOrder/list?order_no="+order_no
             +"&status="+status
             +"&create_stamp_begin_time="+start_date
             +"&create_stamp_end_time="+end_date;

        dataTable.ajax.url(url).load();
    };
    
});