begin;

create table review (
    id bigserial,
    reviewerID varchar not null,
    asin varchar(10) not null,
    overall double precision not null,
    unixReviewTime bigint,
    unique (reviewerID, asin, overall, unixReviewTime)
);

create index reviewTimeIdx on review using btree (unixReviewTime, asin);

commit;
