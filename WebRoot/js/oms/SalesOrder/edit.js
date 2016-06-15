
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });
    
    
    //form表单校验
	 $("#orderForm").validate({
	        rules: {
	        	consignee_id_name:{
	        		required: true,
	        		digits:true,
	        		rangelength: [15,18]
	        	},
				currency_name:{
			 		required: true,
			 		minlength: 3				//输入长度最小是 10 的字符串
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
		        }
	        },
	        messages: {
	        	consignee_id_name: "身份证号码长度为15位或18位"
	        }
	 });


    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }
        
        $(this).attr('disabled', true);

        var cargo_items_array = salesOrder.buildCargoDetail();
        var order = {
            id: $('#order_id').val(),
            order_no: $('#order_no').val(),  
            custom_id: $('#custom_id').val(),  
            order_time: $('#order_time').val(),  
            goods_value: $('#goods_value').val(),
            freight: $('#freight').val(),
            currency: $('#currency').val(),  
            consignee_id: $('#consignee_id').val(),  
            consignee_type: $('#consignee_type').val(),
            consignee: $('#consignee').val(), 
            consignee_address: $('#consignee_address').val(),  
            consignee_telephone: $('#consignee_telephone').val(),  
            consignee_country: $('#consignee_country').val(),
            province: $('#province').val(),
            city: $('#city').val(), 
            district: $('#district').val(), 
            pro_amount: $('#pro_amount').val(),  
            pro_remark: $('#pro_remark').val(),  
            note: $('#note').val(),
            pay_no: $('#pay_no').val(),
            pay_platform: $('#pay_platform').val(), 
            payer_account: $('#payer_account').val(),  
            payer_name: $('#payer_name').val(),  
            is_pay_pass: $('#is_pay_pass').val(),
            pass_pay_no: $('#pass_pay_no').val(),
            pay_code: $('#pay_code').val(),
            pay_name: $('#pay_name').val(),
            status: $('#status').val(),
            cargo_list: cargo_items_array,
            count_list:salesOrder.buildCountDetail()
        };

        var status = $('#status').val();
        //异步向后台提交数据
        $.post('/salesOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	getUser(order.CREATE_BY);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                $("#order_no").val(order.ORDER_NO);
                if(status=='') {
                	$('#status').val('未上报');
                }
                contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                $('#submitDingDanBtn').attr('disabled', false);
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
    	$.post('/salesOrder/submitDingDan', {order_id:$("#order_id").val()}, function(data){
    		if(data!=null){
    			var message = $(data.ceb_results).attr('message');
    			if(message == '订单写入成功'){
    				$.scojs_message(message , $.scojs_message.TYPE_OK);
    				$('#status').val('订单写入成功');
    			}else{
    				$.scojs_message(message , $.scojs_message.TYPE_FALSE);
    			}	
    		}else{
    			$.scojs_message('上报失败', $.scojs_message.TYPE_FALSE);
    		}
    	});
    });
    
    
    //获取用户信息
    var getUser = function(user_id){
    	if(user_id == '' || user_id == null)
    		return;
    	$.post('/customCompany/getUser', {params:user_id}, function(data){
    		if(data!=null)
    			 $("#creator_name").val(data.C_NAME);	
    	})
    }
    
    
    //通过报关企业获取内容
    $('#custom_id_input').on('blur',function(){
    	var custom_id = $('#custom_id').val();
        if(custom_id!=null)
        	showCustom(custom_id);
    });
    
    //回显报关信息
    var showCustom = function(custom_id){
    	if(custom_id.trim()=="")
    		return;
    	$.post('/salesOrder/getCustomCompany', {params:custom_id}, function(data){
    		if(data!=null){
    			$("#org_code").val(data.ORG_CODE);	
    			$("#warehouse_no").val(data.WAREHOUSE_NO);	
    			$("#ebp_code_cus").val(data.EBP_CODE_CUS);	
    			$("#ebp_code_ciq").val(data.EBP_CODE_CIQ);	
    			$("#ebp_name").val(data.EBP_NAME);	
    			$("#ebc_code_cus").val(data.EBC_CODE_CUS);	
    			$("#ebc_code_ciq").val(data.EBC_CODE_CIQ);	
    			$("#ebc_name").val(data.EBC_NAME);	
    			$("#agent_code_cus").val(data.AGENT_CODE_CUS);	
    			$("#agent_code_ciq").val(data.AGENT_CODE_CIQ);	
    			$("#agent_name").val(data.AGENT_NAME);	
    			$("#ciq_code").val(data.CIQ_CODE);	
    		}
    	})	
    }
    
    //按钮控制
    var order_id = $("#order_id").val()
    if(order_id != ''){
    	 $('#submitDingDanBtn').attr('disabled',false);
    }

} );