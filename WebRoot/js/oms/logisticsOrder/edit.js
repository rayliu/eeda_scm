
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

    //------------save
    $('#saveBtn').click(function(e){
    	//提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }
        
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
            log_no: $('#log_no').val(),  
            country_code: $('#country_code').val(),  
            shipper_country: $('#shipper_country').val(),
            shipper_country: $('#shipper_country').val(),  
            shipper_city: $('#shipper_city').val(),
            shipper: $('#shipper').val(),
            shipper_address: $('#shipper_address').val(),  
            shipper_telephone: $('#shipper_telephone').val(),  
            traf_mode: $('#traf_mode').val(),
            ship_name: $('#ship_name').val(), 
            freight: $('#freight').val(),  
            insure_fee: $('#insure_fee').val(),  
            weight: $('#weight').val(),
            netwt: $('#netwt').val(),
            pack_no: $('#pack_no').val(), 
            parcel_info: $('#parcel_info').val(), 
            goods_info: $('#goods_info').val(),  
            customs_code: $('#customs_code').val(),  
            ciq_code: $('#ciq_code').val(),
            port_code: $('#port_code').val(),
            decl_code: $('#decl_code').val(), 
            supervision_code: $('#supervision_code').val(),  
            destination_port: $('#destination_port').val(),  
            ie_date: $('#ie_date').val(),
            deliver_date: $('#deliver_date').val(),
            trade_mode: $('#trade_mode').val(),
            ps_type: $('#ps_type').val(),
            trans_mode: $('#trans_mode').val(),
            warp_type: $('#warp_type').val(),
            cut_mode: $('#cut_mode').val(),
            ems_no: $('#ems_no').val(),
            report_pay_no: $('#report_pay_no').val(),
            cargo_list: cargo_items_array
        };


        //异步向后台提交数据
        $.post('/logisticsOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
            	$("#creator_name").val(data.CREATE_BY_NAME);	
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                $("#log_no").val(order.LOG_NO);
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
    
} );