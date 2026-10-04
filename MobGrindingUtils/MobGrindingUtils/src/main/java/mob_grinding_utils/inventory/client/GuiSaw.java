package mob_grinding_utils.inventory.client;

import mob_grinding_utils.inventory.server.ContainerSaw;
import mob_grinding_utils.network.BEGuiClick;
import mob_grinding_utils.util.RL;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nonnull;

public class GuiSaw extends MGUScreen<ContainerSaw> {
	private final ContainerSaw container;
	private GuiMGUButton toggleButton;

	public GuiSaw(ContainerSaw containerSaw, Inventory playerInventory, Component title) {
		super(containerSaw, playerInventory, title, RL.mgu("textures/gui/saw_gui.png"));
		this.container = containerSaw;
		imageHeight = 132;
	}

	@Override
	public void init() {
		super.init();
		toggleButton = new GuiMGUButton(leftPos + 58, topPos + 36, GuiMGUButton.Size.WIDE_SHORT, 0, Component.empty(), (button) ->
			PacketDistributor.sendToServer(new BEGuiClick(container.saw.getBlockPos(), 0)));
		addRenderableWidget(toggleButton);
	}

	@Override
	public void render(@Nonnull GuiGraphics gg, int mouseX, int mouseY, float partialTicks) {
		toggleButton.setMessage(Component.literal(container.saw.active ? "Turn Off" : "Turn On"));
		super.render(gg, mouseX, mouseY, partialTicks);
	}
}