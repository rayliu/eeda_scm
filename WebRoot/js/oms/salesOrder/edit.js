
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });
    
    
	$("#orderForm").validate({
        rules: {
        	consignee_id_name:{
        		isIdCardNo:true	
        	},
			currency_name:{
		 		rangelength:[3,3]
		 	},
		 	consignee_country_name:{
		 		rangelength:[3,3]
		 	},
		 	pay_no_name:{
		 		minlength: 5
	        },
	        consignee_type_name:{
	        	rangelength:[1,1]
	        },
	        consignee_telephone_name:{
	        	isPhone:true
	        },
	        freight_name:{
	        	number:true
	        },
	        goods_value_name:{
	        	number:true
	        },
	        pro_amount_name:{
	        	number:true
	        }
        },
        messages: {
        	currency_name: "长度必须为3位字符",
        	consignee_country_name: "长度必须为3位字符",
        	pay_no_name: "长度不能小于5位字符",
        	consignee_type_name: "长度必须为1位字符"
        }
       
	});
	
	
    $.extend($.validator.messages, {
		rangelength: $.validator.format("长度必须为{0}位字符")
	})


    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        } 
        
        $(this).attr('disabled', true);
        //分解收货人省市区的地址编码
        var pro_ci_dis = $('#pro_ci_dis').val();
        var province = pro_ci_dis.substring(0,6);
        var city = pro_ci_dis.substring(7,13);
        var district = pro_ci_dis.substring(14,21);

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
            province: province,
            city: city, 
            district: district, 
            pro_amount: $('#pro_amount').val(),  
            pro_remark: $('#pro_remark').val(),  
            note: $('#note').val(),
            pay_no: $('#pay_no').val(),
            pay_channel: $('#pay_channel').val(),
            pay_platform: $('#pay_platform').val(), 
            payer_account: $('#payer_account').val(),  
            payer_name: $('#payer_name').val(),  
            payer_email: $('#payer_email').val(),  
            payer_phone: $('#payer_phone').val(),  
            is_pay_pass: $('#is_pay_pass').val(),
            pass_pay_no: $('#pass_pay_no').val(),
            pay_code: $('#pay_code').val(),
            pay_name: $('#pay_name').val(),
            status: $('#status').val()==''?'未上报':$('#status').val(),
            cargo_list: cargo_items_array,
            count_list:salesOrder.buildCountDetail()
        };

        var status = $('#status').val();
        //异步向后台提交数据
        $.post('/salesOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	$("#creator_name").val(order.CREATE_BY_NAME);	
            	if($("#order_id").val()=='')
            		$("#create_stamp").val(eeda.getDate());
                $("#order_id").val(order.ID);
                $("#order_no").val(order.ORDER_NO);
                if(status=='') {
                	$('#status').val(order.STATUS);
                }
                contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                $('#payBtn').attr('disabled', false);
                
                //刷新明细表
                salesOrder.refleshItemTable(order.ID);

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
    
    //前往相对于的运输单
    $('#goYunDanBtn').click(function(){
    	var log_id = $("#log_id").val();
    	window.open("/logisticsOrder/edit?id="+log_id);
    });

    
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
    var order_id = $("#order_id").val();
    var status = $("#status").val();
    var pay_no = $("#pay_no").val();
    if(order_id == ''){
    	 $('#saveBtn').attr('disabled',false);
    }else{
    	if(status=='未上报'){
    		if(pay_no==''){
    			$('#saveBtn').attr('disabled',false);
    			$('#payBtn').attr('disabled',false);
    		}else{
    			$('#submitDingDanBtn').attr('disabled',false);
    		}
    	}else{
    		$('#goYunDanBtn').attr('disabled',false);
    	}
    }
    
  
} );