
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });
    
    //非空校验
    $("#orderForm").validate({
    	rules:{
    		country_code_name:{
    			rangelength:[3,3]
    		},
    		shipper_country_name:{
    			rangelength:[3,3]
    		},
    		shipper_city_name:{
    			rangelength:[6,6]
    		},
    		customs_code_name:{
    			rangelength:[4,4]
    		},
    		ciq_code_name:{
    			rangelength:[6,6]
    		},
    		ie_date_name:{
    			dateISO:true
    		},
    		freight_name:{
    			number:true
    		},
    		insure_fee_name:{
    			number:true
    		},
    		weight_name:{
    			number:true
    		},
    		netwt_name:{
    			number:true
    		},
    		pack_no_name:{
    			digits:true
    		},
    		shipper_telephone_name:{
    			isPhone:true
    		}
    	}
    	
    	/*messages: {
    		rangelength: $.validator.format("长度必须为{0}位字符"),
    		country_code_name: "长度必须为3位字符",
    		shipper_country_name: "长度必须为3位字符",
    		shipper_city_name: "长度必须为6位字符",
    		customs_code_name: "长度必须为4位字符"
    	}*/
    });
    
    $.extend($.validator.messages, {
		rangelength: $.validator.format("长度必须为{0}位字符")
	})
	
	//构造主表json
    var buildOrder = function(){
    	var item = {};
    	var orderForm = $('#orderForm input,select,textarea');
    	for(var i = 0; i < orderForm.length; i++){
    		var name = orderForm[i].id;
        	var value =orderForm[i].value;
        	if(name){
        		item[name] = value;
        	}
    	}
        return item;
    }

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
        var cargo_items_array = itemOrder.buildCargoDetail();
        var order = buildOrder();
        order.id = $('#order_id').val();
        order.cargo_list = cargo_items_array

        //异步向后台提交数据
        $.post('/logisticsOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	$("#creator_name").val(data.CREATE_BY_NAME);	
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                $("#log_no").val(order.LOG_NO);

                //韵达快递下单
                if(order.MAIL_NO!='' && order.MAIL_NO!=null)
                	$("#parcel_info").val(order.MAIL_NO);
                
                eeda.contactUrl("edit?id",order.ID);
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
    

    
    //上报运单
    $('#submitBtn').click(function(){
    	 $.post('/logisticsOrder/submitYunDan', {order_id:$("#order_id").val()}, function(data){
    		 if(data!=null){
     			var message = $(data.logistics).attr('message');
     			if(message == '运单写入成功'){
     				$.scojs_message(message , $.scojs_message.TYPE_OK);
     				$('#status').val(message);
     			}else{
     				$.scojs_message(message , $.scojs_message.TYPE_FALSE);
     			}	
     		}else{
     			$.scojs_message('上报失败', $.scojs_message.TYPE_FALSE);
     		}
    	 })
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
    if(order_id == ''){
    	 $('#saveBtn').attr('disabled',false);
    }else{
    	if(status=='未上报' || status=='暂存'){
    		$('#saveBtn').attr('disabled',false);
			$('#submitBtn').attr('disabled',false);
    	}
    }
    
} );