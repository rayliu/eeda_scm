
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $('#amount').blur(function(){
        $('#total_amount').text($(this).val());
    });
    
    

    //------------save
    $('#saveBtn').click(function(e){
        $(this).attr('disabled', true);

        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数据
        if(!$("#orderForm").valid()){
            return;
        }

        var cargo_items_array = salesOrder.buildCargoDetail();
        var order = {
            id: $('#order_id').val(),
            log_no: $('#log_no').val(),  
            country_code: $('#country_code').val(),  
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
            	getUser(order.CREATE_BY);
                $("#create_stamp").val(order.CREATE_STAMP);
                $("#order_id").val(order.ID);
                $("#order_no").val(order.ORDER_NO);
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
    	 $.post('/salesOrder/submitDingDan', {order_id:$("#order_id").val()}, function(data){
    		 
    	 })
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
    
} );