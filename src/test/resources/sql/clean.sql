DO
'
    DECLARE
        statements CURSOR FOR
            SELECT tablename, schemaname FROM pg_tables
            WHERE  schemaname = ''s_box_schema'';
        sequenceStatements CURSOR FOR
            SELECT sequencename FROM pg_sequences
            WHERE schemaname = ''s_box_schema'';
    BEGIN
        FOR stmt IN statements
            LOOP
                EXECUTE ''TRUNCATE TABLE '' || stmt.schemaname || ''.'' || stmt.tablename || '' CASCADE;'';
            END LOOP;
        FOR stmt IN sequenceStatements
            LOOP
            EXECUTE ''ALTER SEQUENCE '' || stmt.sequencename || '' RESTART WITH 1'';
            END LOOP;
    END;
' LANGUAGE PLPGSQL;