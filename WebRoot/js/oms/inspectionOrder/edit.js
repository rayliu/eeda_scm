
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });
    
    
    //form表单校验
/*	 $("#orderForm").validate({
	        rules: {
	        	consignee_id_name:{
	        		rangelength:[15,18],
	        		
	        	},
				currency_name:{
			 		required: true,
			 		rangelength:[3,3]				//输入长度最小是 10 的字符串
			 	},
			 	consignee_country_name:{
			 		rangelength:[3,3]
			 	},
			 	province_name:{
			 		rangelength:[6,6]
			 	},
			 	city_name:{
			 		rangelength:[6,6]
			 	},
			 	district_name:{
			 		rangelength:[6,6]
			 	},
			 	pay_no_name:{
			 		minlength: 5
		        },
		        consignee_type_name:{
		        	rangelength:[1,1]
		        }
	        },
	        messages: {
	        	consignee_id_name: "身份证号码长度为15位或18位",
	        	currency_name: "长度必须为3位字符",
	        	consignee_country_name: "长度必须为3位字符",
	        	province_name: "长度必须为6位字符",
	        	city_name: "长度必须为6位字符",
	        	district_name: "长度必须为6位字符",
	        	pay_no_name: "长度不能小于5位字符",
	        	consignee_type_name: "长度必须为1位字符"
	        }
	       
	 });*/


    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }
        
        $(this).attr('disabled', true);

        var items_array = inspectionOrder.buildCargoDetail();
        var order = {
            id: $('#order_id').val(),
            gate_in_id: $('#gate_in_id').val(),
            warehouse_id: $('#warehouse_id').val(),  
            warehouse_area: $('#warehouse_area').val(),  
            inspection_packing_unit: $('#inspection_packing_unit').val(),  
            shipper: $('#shipper').val(),
            can_check: $('#can_check').val(),
            have_check: $('#have_check').val(),  
            difference: $('#difference').val(),  
            status: $('#status').val()==''?'新建':$('#status').val(),  
            remark: $('#remark').val(),
            item_list:items_array
        };

        var status = $('#status').val();
        //异步向后台提交数据
        $.post('/inspectionOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	$("#creator_name").val(order.CREATOR_NAME);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                $("#status").val(order.STATUS);
                contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
            }else{
                $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
                $('#saveBtn').attr('disabled', false);
            }
        },'json').fail(function() {
            $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
            $('#saveBtn').attr('disabled', false);
          });
    });  
    
    
    //上报订单
    $('#submitDingDanBtn').click(function(){
    	$('#submitDingDanBtn').attr('disabled',true);
    	$.post('/inspectionOrder/submitDingDan', {order_id:$("#order_id").val()}, function(data){
    		if(data!=null){
    			var message = $(data.orders).attr('message');
    			if(message == '订单写入成功'){
    				$.scojs_message(message , $.scojs_message.TYPE_OK);
    				$('#status').val(message);
    			}else{
    				$.scojs_message(message , $.scojs_message.TYPE_FALSE);
    			}	
    		}else{
    			$.scojs_message('上报失败', $.scojs_message.TYPE_FALSE);
    		}
    	});
    });
    
    
    //通过报关企业获取内容
    $('#custom_id_input').on('blur',function(){
    	var custom_id = $('#custom_id').val();
        if(custom_id!=null)
        	showCustom(custom_id);
    });
 
    //按钮控制
    var order_id = $("#order_id").val()
    if(order_id != ''){
    	 $('#submitDingDanBtn').attr('disabled',false);
    }

} );