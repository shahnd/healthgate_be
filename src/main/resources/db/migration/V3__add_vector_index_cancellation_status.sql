ALTER TABLE vector_index_manifests
    MODIFY status ENUM(
        'COMPLETED',
        'FAILED',
        'INDEXING',
        'PENDING',
        'PURGE_FAILED',
        'PURGING',
        'CANCEL_REQUESTED',
        'CANCELLED'
    ) NOT NULL;
