package com.wob.poc.order.adapter.in.integration;

import com.mongodb.client.result.UpdateResult;
import com.wob.poc.order.adapter.out.mongo.OrderDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.integration.aop.ReceiveMessageAdvice;
import org.springframework.messaging.Message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An implementation of {@link ReceiveMessageAdvice} that processes incoming messages
 * to claim documents in a MongoDB collection. This advice is typically applied to a
 * message source in a Spring Integration flow to mark documents as being processed
 * when they are received by the system.
 *
 * This class relies on {@link MongoTemplate} to update the MongoDB documents in the
 * configured "order" collection. Specifically, it operates on documents with an
 * orderStatus of "NEW" and attempts to claim them by updating their status to
 * "PROCESSING" and setting the lastModifiedAt timestamp.
 *
 * The primary responsibility of this class is to filter and update messages from
 * a MongoDB-backed message source. Only successfully claimed documents will be
 * included in the returned message payload. If no documents are successfully
 * claimed, a null message is returned, effectively halting further processing.
 *
 * Constructor Detail:
 * This class is annotated with {@code @RequiredArgsConstructor}, indicating that
 * it requires a {@link MongoTemplate} instance to be provided for initialization.
 *
 * Method Detail:
 *
 * 1. {@code afterReceive(Message<?> result, Object source)}:
 *    Processes the received {@link Message} to claim {@link OrderDocument} objects.
 *    It examines the message payload and filters documents based on specific
 *    criteria (e.g., orderStatus is "NEW"). For each document that meets the
 *    criteria, {@code claimDocument(OrderDocument doc)} is invoked to update its
 *    status.
 *
 *    The method returns a message containing only the successfully claimed
 *    documents, or null if no documents were claimed.
 *
 * 2. {@code claimDocument(OrderDocument doc)}:
 *    A helper method that performs the update operation on a single document in
 *    the MongoDB collection. It constructs a query to identify the document to be
 *    claimed and an update operation to mark it as "PROCESSING" with the current
 *    timestamp. The result of the update operation is used to determine whether
 *    the document was successfully claimed.
 *
 * Typical Use Case:
 *
 * - Applied as an advice in a Spring Integration flow to selectively process messages
 *   retrieved from a MongoDB collection.
 * - Essential in scenarios where processing must be confined to unprocessed documents.
 *
 * Constraints:
 *
 * - Assumes the documents in the MongoDB collection conform to the structure
 *   expected by {@link OrderDocument}.
 * - Prevents concurrent claims on the same document by relying on atomic updates
 *   in MongoDB.
 *
 * Thread Safety:
 *
 * - This class is not thread-safe as it relies on the non-thread-safe {@link MongoTemplate}.
 *   Ensure that the advice is correctly scoped as a bean in Spring to manage concurrency safely.
 */
@RequiredArgsConstructor
public class ClaimingMessageSourceAdvice implements ReceiveMessageAdvice {

    private final MongoTemplate mongoTemplate;

    @Override
    public Message<?> afterReceive(Message<?> result, Object source) {
        if (result != null && result.getPayload() instanceof List<?> documents && !documents.isEmpty()) {
            List<OrderDocument> claimedDocs = new ArrayList<>();
            for (Object item : documents) {
                if (item instanceof OrderDocument doc && claimDocument(doc)) {
                    claimedDocs.add(doc);
                }
            }

            return claimedDocs.isEmpty() ? null : result;
        }

        return null;
    }

    private boolean claimDocument(OrderDocument doc) {
        Query query = new Query(Criteria.where("_id").is(doc.getId()).and("orderStatus").is("NEW"));
        Update update = new Update().set("orderStatus", "PROCESSING").set("lastModifiedAt", new Date());
        UpdateResult res = mongoTemplate.updateFirst(query, update, "order");
        return res.getModifiedCount() > 0;
    }
}
