package org.babyfish.jimmer.sql.fetcher;

import org.babyfish.jimmer.sql.JoinType;
import org.babyfish.jimmer.sql.ast.Predicate;
import org.babyfish.jimmer.sql.ast.table.Props;
import org.babyfish.jimmer.sql.common.AbstractQueryTest;
import org.babyfish.jimmer.sql.filter.Filter;
import org.babyfish.jimmer.sql.filter.FilterArgs;
import org.babyfish.jimmer.sql.model.TreeNodeFetcher;
import org.babyfish.jimmer.sql.model.TreeNodeProps;
import org.babyfish.jimmer.sql.model.TreeNodeTable;
import org.babyfish.jimmer.sql.model.issue888.ItemFetcher;
import org.babyfish.jimmer.sql.model.issue888.StructureFetcher;
import org.babyfish.jimmer.sql.model.issue888.StructureTable;
import org.h2.table.TableFilter;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class RecursiveTest extends AbstractQueryTest {

    @Test
    public void testFindTwoLevel() {
        executeAndExpect(
                getLambdaClient().createQuery(TreeNodeTable.class, (q, node) -> {
                    q.where(node.parent(JoinType.LEFT).isNull());
                    return q.select(
                            node.fetch(
                                    TreeNodeFetcher.$.name().recursiveChildNodes(
                                            it -> it.batch(2).depth(2).filter(args -> {
                                                args.orderBy(args.getTable().id());
                                            })
                                    )
                            )
                    );
                }),
                ctx -> {
                    ctx.sql("select tb_1_.NODE_ID, tb_1_.NAME from TREE_NODE tb_1_ where tb_1_.PARENT_ID is null");
                    ctx.statement(1).sql(
                            "select " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(1L);
                    ctx.statement(2).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(2L, 9L);

                    ctx.rows("[{" +
                            "--->\"id\":1," +
                            "--->\"name\":\"Home\"," +
                            "--->\"childNodes\":[" +
                            "--->--->{" +
                            "--->--->--->\"id\":2," +
                            "--->--->--->\"name\":\"Food\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{\"id\":3,\"name\":\"Drinks\"}," +
                            "--->--->--->--->{\"id\":6,\"name\":\"Bread\"}" +
                            "--->--->--->]" +
                            "--->--->},{" +
                            "--->--->--->\"id\":9," +
                            "--->--->--->\"name\":\"Clothing\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{\"id\":10,\"name\":\"Woman\"}," +
                            "--->--->--->--->{\"id\":18,\"name\":\"Man\"}" +
                            "--->--->--->]" +
                            "--->--->}" +
                            "--->]" +
                            "}]");
                }
        );
    }

    @Test
    public void testFindThreeLevel() {
        executeAndExpect(
                getLambdaClient().createQuery(TreeNodeTable.class, (q, node) -> {
                    q.where(node.parent(JoinType.LEFT).isNull());
                    return q.select(
                            node.fetch(
                                    TreeNodeFetcher.$.name().recursiveChildNodes(
                                            it -> it.batch(2).depth(3).filter(args -> {
                                                args.orderBy(args.getTable().id());
                                            })
                                    )
                            )
                    );
                }),
                ctx -> {
                    ctx.sql("select tb_1_.NODE_ID, tb_1_.NAME from TREE_NODE tb_1_ where tb_1_.PARENT_ID is null");
                    ctx.statement(1).sql(
                            "select " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(1L);
                    ctx.statement(2).sql(
                            "select tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(2L, 9L);
                    ctx.statement(3).sql(
                            "select tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(3L, 6L);
                    ctx.statement(4).sql(
                            "select tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(10L, 18L);
                    ctx.rows("[{" +
                            "--->\"id\":1," +
                            "--->\"name\":\"Home\"," +
                            "--->\"childNodes\":[" +
                            "--->--->{" +
                            "--->--->--->\"id\":2," +
                            "--->--->--->\"name\":\"Food\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{" +
                            "--->--->--->--->--->\"id\":3," +
                            "--->--->--->--->--->\"name\":\"Drinks\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{\"id\":4,\"name\":\"Coca Cola\"}," +
                            "--->--->--->--->--->--->{\"id\":5,\"name\":\"Fanta\"}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->},{" +
                            "--->--->--->--->--->\"id\":6," +
                            "--->--->--->--->--->\"name\":\"Bread\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{\"id\":7,\"name\":\"Baguette\"}," +
                            "--->--->--->--->--->--->{\"id\":8,\"name\":\"Ciabatta\"}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->}" +
                            "--->--->--->]" +
                            "--->--->},{" +
                            "--->--->--->\"id\":9," +
                            "--->--->--->\"name\":\"Clothing\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{" +
                            "--->--->--->--->--->\"id\":10," +
                            "--->--->--->--->--->\"name\":\"Woman\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{\"id\":11,\"name\":\"Casual wear\"}," +
                            "--->--->--->--->--->--->{\"id\":15,\"name\":\"Formal wear\"}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->},{" +
                            "--->--->--->--->--->\"id\":18," +
                            "--->--->--->--->--->\"name\":\"Man\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{\"id\":19,\"name\":\"Casual wear\"}," +
                            "--->--->--->--->--->--->{\"id\":22,\"name\":\"Formal wear\"}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->}" +
                            "--->--->--->]" +
                            "--->--->}" +
                            "--->]" +
                            "}]");
                }
        );
    }

    @Test
    public void testFindUnlimitedLevel() {
        executeAndExpect(
                getLambdaClient().createQuery(TreeNodeTable.class, (q, node) -> {
                    q.where(node.parent(JoinType.LEFT).isNull());
                    return q.select(
                            node.fetch(
                                    TreeNodeFetcher.$.name().recursiveChildNodes(
                                            it -> it.filter(args -> {
                                                args.orderBy(args.getTable().id());
                                            })
                                    )
                            )
                    );
                }),
                ctx -> {
                    ctx.sql("select tb_1_.NODE_ID, tb_1_.NAME from TREE_NODE tb_1_ where tb_1_.PARENT_ID is null");
                    ctx.statement(1).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(1L);
                    ctx.statement(2).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(2L, 9L);
                    ctx.statement(3).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(3L, 6L, 10L, 18L);
                    ctx.statement(4).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(4L, 5L, 7L, 8L, 11L, 15L, 19L, 22L);
                    ctx.statement(5).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(12L, 13L, 14L, 16L, 17L, 20L, 21L, 23L, 24L);
                    ctx.rows("[{" +
                            "--->\"id\":1," +
                            "--->\"name\":" +
                            "--->\"Home\"," +
                            "--->\"childNodes\":[" +
                            "--->--->{" +
                            "--->--->--->\"id\":2," +
                            "--->--->--->\"name\":\"Food\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{" +
                            "--->--->--->--->--->\"id\":3," +
                            "--->--->--->--->--->\"name\":\"Drinks\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{\"id\":4,\"name\":\"Coca Cola\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->{\"id\":5,\"name\":\"Fanta\",\"childNodes\":[]}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->},{" +
                            "--->--->--->--->--->\"id\":6," +
                            "--->--->--->--->--->\"name\":\"Bread\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{\"id\":7,\"name\":\"Baguette\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->{\"id\":8,\"name\":\"Ciabatta\",\"childNodes\":[]}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->}" +
                            "--->--->--->]" +
                            "--->--->},{" +
                            "--->--->--->\"id\":9," +
                            "--->--->--->\"name\":\"Clothing\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{" +
                            "--->--->--->--->--->\"id\":10," +
                            "--->--->--->--->--->\"name\":\"Woman\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{" +
                            "--->--->--->--->--->--->--->\"id\":11," +
                            "--->--->--->--->--->--->--->\"name\":\"Casual wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":12,\"name\":\"Dress\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":13,\"name\":\"Miniskirt\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":14,\"name\":\"Jeans\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->},{" +
                            "--->--->--->--->--->--->--->\"id\":15," +
                            "--->--->--->--->--->--->--->\"name\":\"Formal wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":16,\"name\":\"Suit\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":17,\"name\":\"Shirt\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->},{" +
                            "--->--->--->--->--->\"id\":18," +
                            "--->--->--->--->--->\"name\":\"Man\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{" +
                            "--->--->--->--->--->--->--->\"id\":19," +
                            "--->--->--->--->--->--->--->\"name\":\"Casual wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":20,\"name\":\"Jacket\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":21,\"name\":\"Jeans\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->},{" +
                            "--->--->--->--->--->--->--->\"id\":22," +
                            "--->--->--->--->--->--->--->\"name\":\"Formal wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":23,\"name\":\"Suit\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":24,\"name\":\"Shirt\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->}" +
                            "--->--->--->]" +
                            "--->--->}" +
                            "--->]" +
                            "}]");
                }
        );
    }

    @Test
    public void testFindByDynamicalRecursionStrategy() {
        executeAndExpect(
                getLambdaClient().createQuery(TreeNodeTable.class, (q, node) -> {
                    q.where(node.parent(JoinType.LEFT).isNull());
                    return q.select(
                            node.fetch(
                                    TreeNodeFetcher.$.name().recursiveChildNodes(
                                            it -> it
                                                    .filter(args -> {
                                                        args.orderBy(args.getTable().id());
                                                    })
                                                    .recursive(args ->
                                                            !args.getEntity().name().equals("Drinks")
                                                    )
                                    )
                            )
                    );
                }),
                ctx -> {
                    ctx.sql(
                            "select " +
                                    "tb_1_.NODE_ID, " +
                                    "tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where " +
                                    "tb_1_.PARENT_ID is null"
                    );
                    ctx.statement(1).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(1L);
                    ctx.statement(2).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(2L, 9L);
                    ctx.statement(3).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(6L, 10L, 18L);
                    ctx.statement(4).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(7L, 8L, 11L, 15L, 19L, 22L);
                    ctx.statement(5).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(12L, 13L, 14L, 16L, 17L, 20L, 21L, 23L, 24L);
                    ctx.rows("[{" +
                            "--->\"id\":1," +
                            "--->\"name\":" +
                            "--->\"Home\"," +
                            "--->\"childNodes\":[" +
                            "--->--->{" +
                            "--->--->--->\"id\":2," +
                            "--->--->--->\"name\":\"Food\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{\"id\":3,\"name\":\"Drinks\"}," +
                            "--->--->--->--->{" +
                            "--->--->--->--->--->\"id\":6," +
                            "--->--->--->--->--->\"name\":\"Bread\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{\"id\":7,\"name\":\"Baguette\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->{\"id\":8,\"name\":\"Ciabatta\",\"childNodes\":[]}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->}" +
                            "--->--->--->]" +
                            "--->--->},{" +
                            "--->--->--->\"id\":9," +
                            "--->--->--->\"name\":\"Clothing\"," +
                            "--->--->--->\"childNodes\":[" +
                            "--->--->--->--->{" +
                            "--->--->--->--->--->\"id\":10," +
                            "--->--->--->--->--->\"name\":\"Woman\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{" +
                            "--->--->--->--->--->--->--->\"id\":11," +
                            "--->--->--->--->--->--->--->\"name\":\"Casual wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":12,\"name\":\"Dress\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":13,\"name\":\"Miniskirt\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":14,\"name\":\"Jeans\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->},{" +
                            "--->--->--->--->--->--->--->\"id\":15," +
                            "--->--->--->--->--->--->--->\"name\":\"Formal wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":16,\"name\":\"Suit\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":17,\"name\":\"Shirt\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->},{" +
                            "--->--->--->--->--->\"id\":18," +
                            "--->--->--->--->--->\"name\":\"Man\"," +
                            "--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->{" +
                            "--->--->--->--->--->--->--->\"id\":19," +
                            "--->--->--->--->--->--->--->\"name\":\"Casual wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":20,\"name\":\"Jacket\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":21,\"name\":\"Jeans\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->},{" +
                            "--->--->--->--->--->--->--->\"id\":22," +
                            "--->--->--->--->--->--->--->\"name\":\"Formal wear\"," +
                            "--->--->--->--->--->--->--->\"childNodes\":[" +
                            "--->--->--->--->--->--->--->--->{\"id\":23,\"name\":\"Suit\",\"childNodes\":[]}," +
                            "--->--->--->--->--->--->--->--->{\"id\":24,\"name\":\"Shirt\",\"childNodes\":[]}" +
                            "--->--->--->--->--->--->--->]" +
                            "--->--->--->--->--->--->}" +
                            "--->--->--->--->--->]" +
                            "--->--->--->--->}" +
                            "--->--->--->]" +
                            "--->--->}" +
                            "--->]" +
                            "}]");
                }
        );
    }

    @Test
    public void findOnlyRoot() {
        executeAndExpect(
                getLambdaClient().createQuery(TreeNodeTable.class, (q, node) -> {
                    q.where(node.parent(JoinType.LEFT).isNull());
                    return q.select(
                            node.fetch(
                                    TreeNodeFetcher.$.name().recursiveChildNodes(
                                            it -> it
                                                    .filter(args -> {
                                                        args.orderBy(args.getTable().id());
                                                    })
                                                    .recursive(args ->
                                                            !args.getEntity().name().equals("Home")
                                                    )
                                    )
                            )
                    );
                }),
                ctx -> {
                    ctx.sql("select tb_1_.NODE_ID, tb_1_.NAME from TREE_NODE tb_1_ where tb_1_.PARENT_ID is null");
                    ctx.rows("[{\"id\":1,\"name\":\"Home\"}]");
                }
        );
    }

    @Test
    public void findNullTerminal() {
        executeAndExpect(
                getLambdaClient().createQuery(TreeNodeTable.class, (q, treeNode) -> {
                    q.where(
                            treeNode.id().in(Arrays.asList(12L, 13L, 14L, 16L, 17L))
                    );
                    q.orderBy(treeNode.id());
                    return q.select(
                            treeNode.fetch(
                                    TreeNodeFetcher.$.name().recursiveParent(
                                            it -> it.filter(args -> args.orderBy(args.getTable().id()))
                                    )
                            )
                    );
                }),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID in (?, ?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(12L, 13L, 14L, 16L, 17L);
                    ctx.statement(1).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(11L, 15L);
                    ctx.statement(2).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(10L);
                    ctx.statement(3).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(9L);
                    ctx.statement(4).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(1L);
                }
        );
    }

    @Test
    public void testMultipleRecursions() {
        TreeNodeTable table = TreeNodeTable.$;
        executeAndExpect(
                getSqlClient()
                        .createQuery(table)
                        .where(table.id().eq(10L))
                        .select(
                                table.fetch(
                                        TreeNodeFetcher.$
                                                .allScalarFields()
                                                .recursiveParent()
                                                .recursiveChildNodes()
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(10L);
                    ctx.statement(1).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(9L);
                    ctx.statement(2).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(1L);
                    ctx.statement(3).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(10L);
                    ctx.statement(4).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(11L, 15L);
                    ctx.statement(5).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(12L, 13L, 14L, 16L, 17L);
                    ctx.rows(
                            "[{" +
                                    "--->\"id\":10," +
                                    "--->\"name\":\"Woman\"," +
                                    "--->\"parent\":{" +
                                    "--->--->\"id\":9," +
                                    "--->--->\"name\":\"Clothing\"," +
                                    "--->--->\"parent\":{" +
                                    "--->--->--->\"id\":1," +
                                    "--->--->--->\"name\":\"Home\"," +
                                    "--->--->--->\"parent\":null" +
                                    "--->--->}" +
                                    "--->}," +
                                    "--->\"childNodes\":[" +
                                    "--->--->{" +
                                    "--->--->--->\"id\":11," +
                                    "--->--->--->\"name\":\"Casual wear\"," +
                                    "--->--->--->\"childNodes\":[" +
                                    "--->--->--->--->{\"id\":12,\"name\":\"Dress\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":13,\"name\":\"Miniskirt\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":14,\"name\":\"Jeans\",\"childNodes\":[]}" +
                                    "--->--->--->]" +
                                    "--->--->},{" +
                                    "--->--->--->\"id\":15," +
                                    "--->--->--->\"name\":\"Formal wear\"," +
                                    "--->--->--->\"childNodes\":[" +
                                    "--->--->--->--->{\"id\":16,\"name\":\"Suit\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":17,\"name\":\"Shirt\",\"childNodes\":[]}" +
                                    "--->--->--->]" +
                                    "--->--->}" +
                                    "--->]" +
                                    "}]"
                    );
                }
        );
    }

    @Test
    public void testMultipleRecursionsWithFilter() {
        TreeNodeTable table = TreeNodeTable.$;
        executeAndExpect(
                getSqlClient(it -> {
                    it.addFilters(new Filter<TreeNodeProps>() {
                        @Override
                        public void filter(FilterArgs<TreeNodeProps> args) {
                            args.where(
                                    Predicate.and(
                                            args.getTable().parentId().isNotNull(),
                                            args.getTable().name().ne("Miniskirt")
                                    )
                            );
                        }
                    });
                })
                        .createQuery(table)
                        .where(table.id().eq(10L))
                        .select(
                                table.fetch(
                                        TreeNodeFetcher.$
                                                .allScalarFields()
                                                .recursiveParent()
                                                .recursiveChildNodes()
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ? " +
                                    "and tb_1_.PARENT_ID is not null " +
                                    "and tb_1_.NAME <> ?"
                    ).variables(10L, "Miniskirt");
                    ctx.statement(1).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ? " +
                                    "and tb_1_.PARENT_ID is not null " +
                                    "and tb_1_.NAME <> ?"
                    ).variables(9L, "Miniskirt");
                    ctx.statement(2).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ? " +
                                    "and tb_1_.PARENT_ID is not null " +
                                    "and tb_1_.NAME <> ?"
                    ).variables(1L, "Miniskirt");
                    ctx.statement(3).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "and tb_1_.PARENT_ID is not null " +
                                    "and tb_1_.NAME <> ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(10L, "Miniskirt");
                    ctx.statement(4).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "and tb_1_.PARENT_ID is not null " +
                                    "and tb_1_.NAME <> ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(11L, 15L, "Miniskirt");
                    ctx.statement(5).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?) " +
                                    "and tb_1_.PARENT_ID is not null " +
                                    "and tb_1_.NAME <> ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(12L, 14L, 16L, 17L, "Miniskirt");
                    ctx.rows(
                            "[{" +
                                    "--->\"id\":10," +
                                    "--->\"name\":\"Woman\"," +
                                    "--->\"parent\":{" +
                                    "--->--->\"id\":9," +
                                    "--->--->\"name\":\"Clothing\"," +
                                    "--->--->\"parent\":null" +
                                    "--->}," +
                                    "--->\"childNodes\":[" +
                                    "--->--->{" +
                                    "--->--->--->\"id\":11," +
                                    "--->--->--->\"name\":\"Casual wear\"," +
                                    "--->--->--->\"childNodes\":[" +
                                    "--->--->--->--->{\"id\":12,\"name\":\"Dress\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":14,\"name\":\"Jeans\",\"childNodes\":[]}" +
                                    "--->--->--->]" +
                                    "--->--->},{" +
                                    "--->--->--->\"id\":15," +
                                    "--->--->--->\"name\":\"Formal wear\"," +
                                    "--->--->--->\"childNodes\":[" +
                                    "--->--->--->--->{\"id\":16,\"name\":\"Suit\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":17,\"name\":\"Shirt\",\"childNodes\":[]}" +
                                    "--->--->--->]" +
                                    "--->--->}" +
                                    "--->]" +
                                    "}]"
                    );
                }
        );
    }

    @Test
    public void testMultipleRecursionsWithEmptyFilterForIssue677() {
        TreeNodeTable table = TreeNodeTable.$;
        executeAndExpect(
                getSqlClient(it -> {
                    it.addFilters(new Filter<TreeNodeProps>() {
                        @Override
                        public void filter(FilterArgs<TreeNodeProps> args) {
                            // Do nothing
                        }
                    });
                })
                        .createQuery(table)
                        .where(table.id().eq(10L))
                        .select(
                                table.fetch(
                                        TreeNodeFetcher.$
                                                .allScalarFields()
                                                .recursiveParent()
                                                .recursiveChildNodes()
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(10L);
                    ctx.statement(1).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(9L);
                    ctx.statement(2).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME, tb_1_.PARENT_ID " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.NODE_ID = ?"
                    ).variables(1L);
                    ctx.statement(3).sql(
                            "select tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(10L);
                    ctx.statement(4).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(11L, 15L);
                    ctx.statement(5).sql(
                            "select " +
                                    "tb_1_.PARENT_ID, " +
                                    "tb_1_.NODE_ID, tb_1_.NAME " +
                                    "from TREE_NODE tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?, ?, ?) " +
                                    "order by tb_1_.NODE_ID asc"
                    ).variables(12L, 13L, 14L, 16L, 17L);
                    
                    ctx.rows(
                            "[{" +
                                    "--->\"id\":10," +
                                    "--->\"name\":\"Woman\"," +
                                    "--->\"parent\":{" +
                                    "--->--->\"id\":9," +
                                    "--->--->\"name\":\"Clothing\"," +
                                    "--->--->\"parent\":{" +
                                    "--->--->--->\"id\":1," +
                                    "--->--->--->\"name\":\"Home\"," +
                                    "--->--->--->\"parent\":null" +
                                    "--->--->}" +
                                    "--->}," +
                                    "--->\"childNodes\":[" +
                                    "--->--->{" +
                                    "--->--->--->\"id\":11," +
                                    "--->--->--->\"name\":\"Casual wear\"," +
                                    "--->--->--->\"childNodes\":[" +
                                    "--->--->--->--->{\"id\":12,\"name\":\"Dress\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":13,\"name\":\"Miniskirt\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":14,\"name\":\"Jeans\",\"childNodes\":[]}" +
                                    "--->--->--->]" +
                                    "--->--->},{" +
                                    "--->--->--->\"id\":15," +
                                    "--->--->--->\"name\":\"Formal wear\"," +
                                    "--->--->--->\"childNodes\":[" +
                                    "--->--->--->--->{\"id\":16,\"name\":\"Suit\",\"childNodes\":[]}," +
                                    "--->--->--->--->{\"id\":17,\"name\":\"Shirt\",\"childNodes\":[]}" +
                                    "--->--->--->]" +
                                    "--->--->}" +
                                    "--->]" +
                                    "}]"
                    );
                }
        );
    }

    @Test
    public void testIssue888() {
        StructureTable table = StructureTable.$;
        executeAndExpect(
                getSqlClient()
                        .createQuery(table)
                        .where(table.id().eq(1L))
                        .select(
                                table.fetch(
                                        StructureFetcher.$
                                                .allScalarFields()
                                                .items(
                                                        ItemFetcher.$
                                                                .allScalarFields()
                                                                .recursiveChildItems(),
                                                        cfg -> cfg.filter(
                                                                args -> args.where(args.getTable().parentId().isNull())
                                                        )
                                                )
                                )
                        ),
                ctx -> {
                    ctx.sql(
                            "select tb_1_.ID, tb_1_.NAME " +
                                    "from issue888_structure " +
                                    "tb_1_ where tb_1_.ID = ?"
                    ).variables(1L);
                    ctx.statement(1).sql(
                            "select tb_1_.ID, tb_1_.NAME " +
                                    "from issue888_item tb_1_ " +
                                    "where tb_1_.STRUCTURE_ID = ? " +
                                    "and tb_1_.PARENT_ID is null"
                    ).variables(1L);
                    ctx.statement(2).sql(
                            "select tb_1_.ID, tb_1_.NAME " +
                                    "from issue888_item tb_1_ " +
                                    "where tb_1_.PARENT_ID = ? " +
                                    "order by tb_1_.ID asc"
                    ).variables(1L);
                    ctx.statement(3).sql(
                            "select tb_1_.PARENT_ID, tb_1_.ID, tb_1_.NAME " +
                                    "from issue888_item tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?) " +
                                    "order by tb_1_.ID asc"
                    ).variables(4L, 5L);
                    ctx.statement(4).sql(
                            "select tb_1_.PARENT_ID, tb_1_.ID, tb_1_.NAME " +
                                    "from issue888_item tb_1_ " +
                                    "where tb_1_.PARENT_ID in (?, ?, ?) " +
                                    "order by tb_1_.ID asc"
                    ).variables(2L, 3L, 6L);
                    ctx.row(
                            0,
                            "{" +
                                    "--->\"id\":1,\"name\":\"test\",\"items\":[" +
                                    "--->--->{" +
                                    "--->--->--->\"id\":1,\"name\":\"item1\"," +
                                    "--->--->--->\"childItems\":[" +
                                    "--->--->--->--->{" +
                                    "--->--->--->--->--->\"id\":4,\"name\":\"child-item2\",\"childItems\":[" +
                                    "--->--->--->--->--->--->{\"id\":2,\"name\":\"sub-child-item2\",\"childItems\":[]}," +
                                    "--->--->--->--->--->--->{\"id\":3,\"name\":\"sub-child-item3\",\"childItems\":[]}" +
                                    "--->--->--->--->--->]" +
                                    "--->--->--->--->},{" +
                                    "--->--->--->--->--->\"id\":5,\"name\":\"child-item1\",\"childItems\":[" +
                                    "--->--->--->--->--->--->{\"id\":6,\"name\":\"sub-child-item1\",\"childItems\":[]}" +
                                    "--->--->--->--->--->]" +
                                    "--->--->--->--->}" +
                                    "--->--->--->]" +
                                    "--->--->}" +
                                    "--->]" +
                                    "}"
                    );
                }
        );
    }
}
