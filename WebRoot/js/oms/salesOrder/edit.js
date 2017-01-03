
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    eeda.openMenu();
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });
    
	$("#orderForm").validate({
        rules: {
        	buyer_id_number_name:{
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
	        },
	        weight_name:{
	        	number:true
	        },
	        net_weight_name:{
	        	number:true
	        },
	        insure_fee_name:{
	        	number:true
	        },
	        pack_no_name:{
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
	
	//构造主表json
    var buildOrder = function(){
    	var item = {};
    	item.id = $('#order_id').val();
    	var orderForm = $('#orderForm input,#orderForm select,#orderForm textarea');
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
        	$.scojs_message('必填字段未填写完整' , $.scojs_message.TYPE_ERROR);
            return;
        } 
        
        if($('#trade_mode').val()=='9610'){
        	if($('#voyage_no').val().trim()=='' || $('#bill_no').val().trim()==''){
        		$.scojs_message('贸易方式为直购进口“9610”时，[航班航次号]不能为空,[提运单号]不能为空"' , $.scojs_message.TYPE_ERROR);
                return;
        	}
        }
        
        $(this).attr('disabled', true);
        //分解收货人省市区的地址编码
        var pro_ci_dis = $('#pro_ci_dis').val();
        var province = pro_ci_dis.substring(0,6);
        var city = pro_ci_dis.substring(7,13);
        var district = pro_ci_dis.substring(14,21);

        var order = {};
        order = buildOrder();
        order.province = province;
        order.city = city;
        order.district = district;
        order.cargo_list = salesOrder.buildCargoDetail();
        order.count_list = salesOrder.buildCountDetail();

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
                eeda.contactUrl("edit?id",order.ID);
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
    		if(data != null){
    			var status = data.SUBMIT_STATUS;
    			if(status == '消息接受成功!'){
    				$.scojs_message(status , $.scojs_message.TYPE_OK);
    				setTimeout(function(){
    	            			$.post('/salesOrder/querySubMsg', {order_id:order_id}, function(data){
    	            				if(data){
    	            					if(data.ERROR_MSG!='直购订单写入成功'){
        	            					$.scojs_message(data.ERROR_MSG, $.scojs_message.TYPE_ERROR);
        	            					$('#submitDingDanBtn').attr('disabled',false);
        	            				}else{
        	            					$.scojs_message(data.ERROR_MSG, $.scojs_message.TYPE_OK);
        	            					$('#status').val("已上报");
        	            				}
        	            				$('#submit_status').val(data.SUBMIT_STATUS);
        	            				$('#error_msg').val(data.ERROR_MSG);
    	            				}
    	            			});
    	            		}
    	            		,2000);
    			}else{
    				$.scojs_message(status , $.scojs_message.TYPE_FALSE);
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
    
    
    //前往直购入库单
    $('#goStorageInBtn').click(function(){
    	var log_id = $("#log_id").val();
    	window.open("/storageInOrder/create?order_id="+$("#order_id").val());
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
    
    $('#buyer_id_number').on('input',function(){
    	var value = $(this).val();
    	$('#buyer_regno').val(value);
    });
    
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
    	}
    	if($("#log_id").val() != ''){
    		$("#goYunDanBtn").show();
    	}
    }
    
  
} );