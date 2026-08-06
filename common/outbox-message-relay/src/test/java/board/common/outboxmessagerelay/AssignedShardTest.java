package board.common.outboxmessagerelay;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AssignedShardTest {

    @Test
    void ofTest() {
        List<String> appIds = List.of("appId1", "appId2", "appId3");
        long shardCount = 65L;

        AssignedShard assignedShard1 = AssignedShard.of(appIds.get(0), appIds, shardCount);
        AssignedShard assignedShard2 = AssignedShard.of(appIds.get(1), appIds, shardCount);
        AssignedShard assignedShard3 = AssignedShard.of(appIds.get(2), appIds, shardCount);
        AssignedShard assignedShard4 = AssignedShard.of("invalid", appIds, shardCount);

        List<Long> result = Stream.of(
                        assignedShard1.getShards(),
                        assignedShard2.getShards(),
                        assignedShard3.getShards(),
                        assignedShard4.getShards()
                )
                .flatMap(List::stream)
                .toList();

        assertThat(result).hasSize((int) shardCount);

        for (int i = 0; i < result.size(); i++) {
            assertThat(result.get(i)).isEqualTo(i);
        }

        assertThat(assignedShard4.getShards()).isEmpty();
    }
}
