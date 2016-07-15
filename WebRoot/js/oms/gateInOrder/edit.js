
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    //form表单校验
//	 $("#orderForm").validate({
//	        rules: {
//	        	consignee_id_name:{
//	        		rangelength:[15,18],
//	        		
//	        	}
//	        },
//	        messages: {
//	        	consignee_id_name: "身份证号码长度为15位或18位"
//	        }
//	       
//	 });


    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }
        
        $(this).attr('disabled', true);

        var items_array = gateInOrder.buildCargoDetail();
        var order = {
            id: $('#order_id').val(),
            order_no: $('#order_no').val(),
            warehouse_id: $('#warehouse_id').val(),  
            consignor: $('#consignor').val(),  
            order_type: $('#order_type').val(),  
            pre_storage_begin_time: $('#pre_storage_date_begin_time').val(),
            pre_storage_end_time: $('#pre_storage_date_end_time').val(),
            goods_type: $('#goods_type').val(),  
            storage_type: $('#storage_type').val(),  
            service_detail: $('#service_detail').val(),
            status: $('#status').val()==''?'暂存':$('#status').val(), 
            so_no: $('#so_no').val(),  
            po_no: $('#po_no').val(),  
            customer_refer_no: $('#customer_refer_no').val(),
            asn_no: $('#asn_no').val(),
            pickup_no: $('#pickup_no').val(), 
            send_no: $('#send_no').val(), 
            carrier: $('#carrier').val(),  
            carrier_no: $('#carrier_no').val(),  
            transport_clause: $('#transport_clause').val(),
            transport_type: $('#transport_type').val(),
            route_from: $('#route_from').val(), 
            route_to: $('#route_to').val(),  
            loading_port: $('#loading_port').val(),  
            discharge_port: $('#discharge_port').val(),
            etd: $('#etd').val(),
            eta: $('#eta').val(),
            pre_packing_no: $('#pre_packing_no').val(),
            packing_unit: $('#packing_unit').val(),
            pre_number: $('#pre_number').val(),
            batch_no: $('#batch_no').val(),
            item_list: items_array
        };

        var status = $('#status').val();
        var order_id = $('#order_id').val();
        //异步向后台提交数据
        $.post('/gateInOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	$("#creator_name").val(data.CREATE_BY_NAME);
            	if(order_id=='')
            		$("#create_stamp").val(eeda.getDate());
                $("#order_id").val(order.ID);
                $("#order_no").val(order.ORDER_NO);
				$('#status').val(order.STATUS);
				contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                $('#confirmBtn').attr('disabled', false);
                $('#canselBtn').attr('disabled', false);
                
                //异步刷新字表
                gateInOrder.refleshTable(order.ID);
            }else{
                $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
                $('#saveBtn').attr('disabled', false);
            }
        },'json').fail(function() {
            $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
            $('#saveBtn').attr('disabled', false);
          });
    });  
    
    //确认按钮
    $('#confirmBtn').click(function(e){
    	e.preventDefault();
    	var self = $(this);
    	self.attr('disabled',true);
    	var order_id = $("#order_id").val();
    	$.post('/gateInOrder/confirmOrder', {params:order_id}, function(data){
    		if(data.ID){
    			$('#status').val(data.STATUS);
    			$('#saveBtn').attr('disabled', true);
    			$.scojs_message('确认成功', $.scojs_message.TYPE_OK);
    		}else{
    			$.scojs_message('确认失败', $.scojs_message.TYPE_ERROR);
    			self.attr('disabled',false);
    		}
    	})
    })
    
    //取消按钮
    $('#canselBtn').click(function(e){
    	e.preventDefault();
    	var self = $(this);
    	self.attr('disabled',true);
    	var order_id = $("#order_id").val();
    	$.post('/gateInOrder/canselOrder', {params:order_id}, function(data){
    		if(data.ID){
    			$('#status').val(data.STATUS);
    			$('#saveBtn').attr('disabled', true);
    			$('#confirmBtn').attr('disabled', true);
    			$.scojs_message('取消成功', $.scojs_message.TYPE_OK);
    		}else{
    			$.scojs_message('取消失败', $.scojs_message.TYPE_ERROR);
    			self.attr('disabled',false);
    		}
    	})
    })
    
    //按钮控制
    var order_id = $("#order_id").val();
    var status = $("#status").val();
    if(order_id != ''){
    	if(status=='暂存'){
    		$('#saveBtn').attr('disabled', false);
    		$('#confirmBtn').attr('disabled', false);
    		$('#canselBtn').attr('disabled', false);
    	}else if(status=='已确认'){
    		$('#canselBtn').attr('disabled', false);
    	}
    }else{
    	$('#saveBtn').attr('disabled', false);
    }
} );