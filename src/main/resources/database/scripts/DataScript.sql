-- Insert Users (some new users who aren't in groups yet)
INSERT INTO users (name, contact, email, password) VALUES
('Bob Johnson', '234-567-8901', 'bob@example.com', 'bPYV1byqx3g1Ko8fM2DSPwLzTsGC4lmJf9bOSF14cNQ='),
('Charlie Brown', '345-678-9012', 'charlie@example.com', 'WQasNhoTfi0oZGXNZYjrtaw/WulVABEAvEFXfD11F2Q='),
('David Wilson', '456-789-0123', 'david@example.com', 'uXhzpA9zq+3Y1oWnzV5fheSpz7g+rCaIZkCggThQEis='),
('Eva Adams', '567-890-1234', 'eva@example.com', 'iyyG6pzy6k61F/0eBrdPOZ5/7A/vkuO0gqbPLisJICM='),
('Grace Lee', '678-901-2345', 'grace@example.com', 'WYoaQAwd/fNpdOadfhvJhZPy4VAV7tjpt+R6g7MWk9U='),
('Hank Green', '789-012-3456', 'hank@example.com', 'WGCDbo8T/Jg3U5pZfUCGv8ApnlStkhSNVFOLXD/u+3w='),
('Ivy Nguyen', '890-123-4567', 'ivy@example.com',       'V/Prq2PxVv2Pd2umRaVdljYKFe7/yLDkr+TAX6iCGao='),
('Jack Black', '901-234-5678', 'jack@example.com',       'kyPdZ4bry/OshzV8x4uhq/2mz15VzQEJe5DUoobKyQ4='),
('Karen White', '012-345-6789', 'karen@example.com',     'qkqeoD/KwVtfxjyUmsNOew/ReQZxasO45YxZnNxaUvA='),
('Liam King', '012-345-1111', 'liam@example.com',        'U9RTsMCLaziukVFdyI0l++zdHWAB8CJBlinfhE+LpDM='),
('Mia Scott', '012-345-2222', 'mia@example.com',         's9F+u+Tyt10ntjCc+q4Uh7ZnMBpzlR59UjoDnNLf4RA=');


-- Insert Groups
INSERT INTO groups (name, owner_id) VALUES
                                        ('Friends', 1),
                                        ('Family', 2),
                                        ('Work', 3),
                                        ('Book Club', 4),
                                        ('Yoga Class', 5),
                                        ('Gaming Squad', 6);

-- Insert Group Members
INSERT INTO group_members (group_id, user_id) VALUES
                                                  (1, 1),
                                                  (1, 2),
                                                  (1, 3),
                                                  (2, 2),
                                                  (2, 4),
                                                  (3, 3),
                                                  (3, 4),
                                                  (1, 5),
                                                  (2, 3),
                                                  (2, 5),
                                                  (3, 5),
                                                  (4, 4),
                                                  (4, 1),
                                                  (5, 5),
                                                  (5, 6),
                                                  (6, 7),
                                                  (6, 8),
                                                  (6, 9);

INSERT INTO invites (group_id, invitee_id, inviter_id) VALUES
                                                           (1, 10, 1),
                                                           (2, 6, 2),
                                                           (3, 10, 3),
                                                           (4, 11, 4),
                                                           (5, 11, 5),
                                                           (6, 12, 6);

INSERT INTO expenses (description, value, paid_by, group_id) VALUES
                                                                 ('Dinner at Pizza Place', 120.00, 1, 1),
                                                                 ('Family Outing', 200.00, 2, 2),
                                                                 ('Office Supplies', 150.00, 3, 3),
                                                                 ('Dinner at Italian Restaurant', 180.00, 5, 1),
                                                                 ('Yoga Mats Purchase', 300.00, 5, 5),
                                                                 ('Game Tournament Fee', 500.00, 7, 6),
                                                                 ('Books for Book Club', 90.00, 4, 4);

INSERT INTO payments (payer, value, expense_id) VALUES
                                                    (2, 20.00, 1),
                                                    (3, 20.00, 1),
                                                    (2, 50.00, 2),
                                                    (4, 50.00, 2),
                                                    (4, 25.00, 3),
                                                    (5, 25.00, 3),
                                                    (1, 30.00, 4),
                                                    (2, 30.00, 4),
                                                    (5, 75.00, 5),
                                                    (6, 75.00, 5),
                                                    (8, 50.00, 6),
                                                    (9, 50.00, 6),
                                                    (1, 20.00, 7);


INSERT INTO expense_shares (expense_id, user_id, share, paid, group_id) VALUES
                                                                            (1, 2, 40.00, false, 1),
                                                                            (1, 3, 40.00, false, 1),
                                                                            (1, 5, 40.00, false, 1),
                                                                            (2, 4, 66.67, false, 2),
                                                                            (2, 3, 66.67, false, 2),
                                                                            (2, 5, 66.67, false, 2),
                                                                            (3, 4, 75.00, false, 3),
                                                                            (3, 5, 75.00, false, 3),
                                                                            (4, 2, 60.00, false, 1),
                                                                            (4, 3, 60.00, false, 1),
                                                                            (4, 5, 60.00, false, 1),
                                                                            (5, 6, 150.00, false, 5),
                                                                            (6, 8, 166.67, false, 6),
                                                                            (6, 9, 166.67, false, 6),
                                                                            (7, 1, 30.00, false, 4);
