
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
	        		rangelength:[15,18],
	        		
	        	},
				currency_name:{
			 		required: true,
			 		rangelength:[3,3]
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
            status: $('#status').val()==''?'新建':$('#status').val(), 
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
                if(status=='') {
                	$('#status').val('未上报');
                }
                contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                
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