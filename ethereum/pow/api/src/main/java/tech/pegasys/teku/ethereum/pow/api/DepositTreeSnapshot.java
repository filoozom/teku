/*
 * Copyright ConsenSys Software Inc., 2022
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package tech.pegasys.teku.ethereum.pow.api;

import java.util.List;
import java.util.stream.Collectors;
import org.apache.tuweni.bytes.Bytes32;
import tech.pegasys.teku.infrastructure.json.types.DeserializableTypeDefinition;
import tech.pegasys.teku.infrastructure.ssz.SszList;
import tech.pegasys.teku.infrastructure.ssz.containers.Container3;
import tech.pegasys.teku.infrastructure.ssz.containers.ContainerSchema3;
import tech.pegasys.teku.infrastructure.ssz.primitive.SszBytes32;
import tech.pegasys.teku.infrastructure.ssz.primitive.SszUInt64;
import tech.pegasys.teku.infrastructure.ssz.schema.SszListSchema;
import tech.pegasys.teku.infrastructure.ssz.schema.SszPrimitiveSchemas;
import tech.pegasys.teku.infrastructure.ssz.tree.TreeNode;
import tech.pegasys.teku.infrastructure.unsigned.UInt64;

public class DepositTreeSnapshot
    extends Container3<DepositTreeSnapshot, SszList<SszBytes32>, SszUInt64, SszBytes32> {

  private static class DepositTreeSnapshotSchema
      extends ContainerSchema3<DepositTreeSnapshot, SszList<SszBytes32>, SszUInt64, SszBytes32> {

    protected DepositTreeSnapshotSchema() {
      super(
          "DepositTreeSnapshot",
          namedSchema(
              "finalized",
              SszListSchema.create(SszPrimitiveSchemas.BYTES32_SCHEMA, Integer.MAX_VALUE)),
          namedSchema("deposits", SszPrimitiveSchemas.UINT64_SCHEMA),
          namedSchema("execution_block_hash", SszPrimitiveSchemas.BYTES32_SCHEMA));
    }

    @Override
    public DepositTreeSnapshot createFromBackingNode(final TreeNode node) {
      return new DepositTreeSnapshot(this, node);
    }

    @SuppressWarnings("unchecked")
    public SszListSchema<SszBytes32, ? extends SszList<SszBytes32>> getFinalizedSchema() {
      return (SszListSchema<SszBytes32, ? extends SszList<SszBytes32>>) getChildSchema(0);
    }
  }

  private static final DepositTreeSnapshotSchema SCHEMA = new DepositTreeSnapshotSchema();

  public static DeserializableTypeDefinition<DepositTreeSnapshot> getJsonTypeDefinition() {
    return SCHEMA.getJsonTypeDefinition();
  }

  public DepositTreeSnapshot(
      final List<Bytes32> finalized, final long deposits, final Bytes32 executionBlockHash) {
    super(
        SCHEMA,
        finalized.stream().map(SszBytes32::of).collect(SCHEMA.getFinalizedSchema().collector()),
        SszUInt64.of(UInt64.valueOf(deposits)),
        SszBytes32.of(executionBlockHash));
  }

  private DepositTreeSnapshot(final DepositTreeSnapshotSchema schema, final TreeNode node) {
    super(schema, node);
  }

  public List<Bytes32> getFinalized() {
    final SszList<SszBytes32> value = getAny(0);
    return value.asList().stream().map(SszBytes32::get).collect(Collectors.toList());
  }

  public long getDeposits() {
    final SszUInt64 value = getAny(1);
    return value.longValue();
  }

  public Bytes32 getExecutionBlockHash() {
    final SszBytes32 value = getAny(2);
    return value.get();
  }
}
