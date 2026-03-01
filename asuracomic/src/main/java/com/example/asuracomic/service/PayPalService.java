package com.example.asuracomic.service;

import com.example.asuracomic.entity.CoinPackage;
import com.example.asuracomic.entity.User;
import com.example.asuracomic.repository.CoinPackageRepository;
import com.example.asuracomic.repository.UserRepository;
import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PayPalService {

    @Autowired
    private PayPalHttpClient payPalHttpClient;
    @Autowired
    private CoinPackageRepository coinPackageRepository;
    @Autowired
    private UserRepository userRepository;

    public Order createOrder(Double totalAmount, String returnUrl, String cancelUrl) throws IOException {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.checkoutPaymentIntent("CAPTURE");

        // PayPal chủ yếu dùng USD cho tk cá nhân, nên quy đổi VNĐ sang USD
        AmountWithBreakdown amount = new AmountWithBreakdown()
                .currencyCode("USD")
                .value(String.format("%.2f", totalAmount));

        PurchaseUnitRequest purchaseUnitRequest = new PurchaseUnitRequest().amountWithBreakdown(amount);
        List<PurchaseUnitRequest> purchaseUnitRequests = new ArrayList<>();
        purchaseUnitRequests.add(purchaseUnitRequest);
        orderRequest.purchaseUnits(purchaseUnitRequests);

        ApplicationContext applicationContext = new ApplicationContext()
                .returnUrl(returnUrl)
                .cancelUrl(cancelUrl)
                .shippingPreference("NO_SHIPPING"); // Không cần địa chỉ ship vì là coin
        orderRequest.applicationContext(applicationContext);

        OrdersCreateRequest request = new OrdersCreateRequest().requestBody(orderRequest);
        return payPalHttpClient.execute(request).result();
    }

    public HttpResponse<Order> captureOrder(String orderId) throws IOException {
        OrdersCaptureRequest request = new OrdersCaptureRequest(orderId);
        return payPalHttpClient.execute(request);
    }




    public List<CoinPackage> findAll() {
        return coinPackageRepository.findAll();
    }
    public User findByUserName(String userName) {
        // Trả về User nếu tìm thấy, nếu không trả về null
        return userRepository.findByUsername(userName).orElse(null);
    }
}