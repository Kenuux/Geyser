/*
 * Copyright (c) 2019-2021 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.translator.protocol.java.inventory;

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack;
import com.github.steveice10.mc.protocol.data.game.inventory.VillagerTrade;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.inventory.ClientboundMerchantOffersPacket;
import com.nukkitx.nbt.NbtMap;
import com.nukkitx.nbt.NbtMapBuilder;
import com.nukkitx.nbt.NbtType;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerType;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.UpdateTradePacket;
import org.geysermc.geyser.entity.type.Entity;
import org.geysermc.geyser.inventory.Inventory;
import org.geysermc.geyser.inventory.MerchantContainer;
import org.geysermc.geyser.registry.type.ItemMapping;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.translator.inventory.item.ItemTranslator;
import org.geysermc.geyser.translator.protocol.PacketTranslator;
import org.geysermc.geyser.translator.protocol.Translator;

import java.util.ArrayList;
import java.util.List;

@Translator(packet = ClientboundMerchantOffersPacket.class)
public class JavaMerchantOffersTranslator extends PacketTranslator<ClientboundMerchantOffersPacket> {

    @Override
    public void translate(GeyserSession session, ClientboundMerchantOffersPacket packet) {
        Inventory openInventory = session.getOpenInventory();
        if (!(openInventory instanceof MerchantContainer merchantInventory && openInventory.getId() == packet.getContainerId())) {
            return;
        }

        // No previous inventory was closed -> no need of queuing the merchant inventory
        if (!openInventory.isPending()) {
            merchantInventory.openMerchant(session, packet, merchantInventory);
            return;
        }

        // The inventory is declared as pending due to previous closing inventory -> leads to an incorrect order of execution
        // Handled in BedrockContainerCloseTranslator
        merchantInventory.setPendingOffersPacket(packet);
    }
}
