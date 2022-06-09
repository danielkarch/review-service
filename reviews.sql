begin;

create table review (
    id bigserial,
    reviewerID varchar not null,
    asin varchar(10) not null,
    overall real not null,
    unixReviewTime bigint,
    unique (reviewerID, asin, overall, unixReviewTime)
);

commit;
