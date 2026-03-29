package com.capgemini.applicationservice.messaging;

import com.capgemini.applicationservice.service.ApplicationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentMessageListener {
    
    private final ApplicationService applicationService;

    public DocumentMessageListener(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @RabbitListener(queues = "documentUploadQueue")
    public void receiveMessage(String message) {
        System.out.println("Received message from documentUploadQueue: " + message);
        try {
            String[] parts = message.split(",");
            if (parts.length == 2) {
                Long appId = Long.parseLong(parts[0].trim());
                String status = parts[1].trim();
                applicationService.updateStatus(appId, status);
                System.out.println("Successfully updated application " + appId + " to status " + status);
            } else {
                System.err.println("Invalid message format. Expected 'appId,status'. Got: " + message);
            }
        } catch (Exception e) {
            System.err.println("Failed to process message: " + message);
            e.printStackTrace();
        }
    }
}
