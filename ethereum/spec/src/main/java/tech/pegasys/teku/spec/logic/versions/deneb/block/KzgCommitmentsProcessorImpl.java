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

package tech.pegasys.teku.spec.logic.versions.deneb.block;

import java.util.stream.Collectors;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.BeaconBlockBody;
import tech.pegasys.teku.spec.datastructures.blocks.blockbody.versions.deneb.BeaconBlockBodyDeneb;
import tech.pegasys.teku.spec.datastructures.type.SszKZGCommitment;
import tech.pegasys.teku.spec.logic.common.helpers.MiscHelpers;
import tech.pegasys.teku.spec.logic.common.statetransition.exceptions.BlockProcessingException;

public class KzgCommitmentsProcessorImpl implements KzgCommitmentsProcessor {

  private MiscHelpers miscHelpers;

  protected KzgCommitmentsProcessorImpl(final MiscHelpers miscHelpers) {
    this.miscHelpers = miscHelpers;
  }

  @Override
  public void processBlobKzgCommitments(final BeaconBlockBody body)
      throws BlockProcessingException {

    final BeaconBlockBodyDeneb denebBody =
        body.toVersionDeneb()
            .orElseThrow(() -> new BlockProcessingException("Deneb block is required"));

    if (!miscHelpers.verifyKZGCommitmentsAgainstTransactions(
        denebBody.getExecutionPayload().getTransactions().asList(),
        denebBody.getBlobKzgCommitments().stream()
            .map(SszKZGCommitment::getKZGCommitment)
            .collect(Collectors.toUnmodifiableList()))) {
      throw new BlockProcessingException("KZG commitments do not match transactions");
    }
  }
}
