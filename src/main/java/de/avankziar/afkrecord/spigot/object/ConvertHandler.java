package main.java.de.avankziar.afkrecord.spigot.object;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class ConvertHandler
{
	public static ArrayList<PluginUser> convertListI(ArrayList<?> list)
	{
		ArrayList<PluginUser> el = new ArrayList<>();
		for(Object o : list)
		{
			if(o instanceof PluginUser)
			{
				el.add((PluginUser) o);
			} else
			{
				return null;
			}
		}
		return el;
	}
	
	public static ArrayList<TimeRecord> convertListII(ArrayList<?> list)
	{
		ArrayList<TimeRecord> el = new ArrayList<>();
		for(Object o : list)
		{
			if(o instanceof TimeRecord)
			{
				el.add((TimeRecord) o);
			} else
			{
				return null;
			}
		}
		return el;
	}
	
	public static String ToBase64itemStackArray(ItemStack[] items) throws IllegalStateException  //FIN
    {
    	try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            
            dataOutput.writeInt(items.length);
            
            for (int i = 0; i < items.length; i++) 
            {
                dataOutput.writeObject(items[i]);
            }
            
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }
    
    public static ItemStack[] FromBase64itemStackArray(String data) throws IOException  //FIN
    {
    	try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) 
            {
            	items[i] = (ItemStack) dataInput.readObject();
            }
            
            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) 
    	{
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
