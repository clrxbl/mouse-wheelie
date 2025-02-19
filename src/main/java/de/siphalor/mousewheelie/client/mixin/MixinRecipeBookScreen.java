package de.siphalor.mousewheelie.client.mixin;

import de.siphalor.mousewheelie.client.Config;
import de.siphalor.mousewheelie.client.util.IRecipeBookGui;
import de.siphalor.mousewheelie.client.util.IRecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public abstract class MixinRecipeBookScreen implements IRecipeBookGui {

	@Shadow @Final protected RecipeBookResults recipesArea;

	@Shadow private int parentWidth;

	@Shadow private int leftOffset;

	@Shadow @Final private List<RecipeGroupButtonWidget> tabButtons;

	@Shadow private RecipeGroupButtonWidget currentTab;

	@Shadow protected abstract void refreshResults(boolean boolean_1);

	@Shadow private int parentHeight;

	@Shadow public abstract boolean isOpen();

	@Override
	public boolean mouseWheelie_scrollRecipeBook(double mouseX, double mouseY, double scrollAmount) {
		if(!this.isOpen())
			return false;
		int top = (this.parentHeight - 166) / 2;
		if(mouseY < top || mouseY >= top + 166)
			return false;
		int left = (this.parentWidth - 147) / 2 - this.leftOffset;
		if(mouseX >= left && mouseX < left + 147) {
			// Ugly approach since assigning the casted value causes a runtime mixin error
			int maxPage = ((IRecipeBookResults) recipesArea).mouseWheelie_getPageCount() - 1;
			((IRecipeBookResults) recipesArea).mouseWheelie_setCurrentPage(MathHelper.clamp((int) (((IRecipeBookResults) recipesArea).mouseWheelie_getCurrentPage() + Math.round(scrollAmount * Config.scrollFactor.value)), 0, maxPage < 0 ? 0 : maxPage));
			((IRecipeBookResults) recipesArea).mouseWheelie_refreshResultButtons();
			return true;
		} else if(mouseX >= left - 30 && mouseX < left) {
			int index = tabButtons.indexOf(currentTab);
			int newIndex = MathHelper.clamp(index + (int) (Math.round(scrollAmount * Config.scrollFactor.value)), 0, tabButtons.size() - 1);
			if(newIndex != index) {
				currentTab.setToggled(false);
				currentTab = tabButtons.get(newIndex);
				currentTab.setToggled(true);
				refreshResults(true);
			}
			return true;
		}
		return false;
	}
}
